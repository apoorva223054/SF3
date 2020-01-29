package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.UsersToRole;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;
import com.nirvanaxp.types.entities.tip.EmployeeMasterHistory;
import com.nirvanaxp.types.entities.tip.EmployeeMaster_;
import com.nirvanaxp.types.entities.user.HEBEmployeeExceptionalReport;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.user.utility.GlobalUsermanagement;
import com.nirvanaxp.user.utility.UserManagementObj;
import com.nirvanaxp.user.utility.UserManagementServiceBean;

public class HEBEmployee {
	// code reference :-
	// https://www.callicoder.com/java-read-write-csv-file-apache-commons-csv/
	// by ap 17-01-18
	// https://gist.github.com/rbrick/f8484965bf774df7b9a5
	private NirvanaLogger logger = new NirvanaLogger(HEBEmployee.class.getName());

	public boolean readFile(EntityManager em, HttpServletRequest httpRequest, EntityManager globalEM, String locationId,
			int accountId, String createdBy) throws IOException {
		boolean result = false;
		String filePath = ConfigFileReader.getPeoplesoftExceptionFilePath();
		// String filePath =
		// "C:/Users/nirvanaxp/Downloads/java-read-write-csv-file-master/java-read-write-csv-file-master/java-csv-file-handling-with-apache-commons-csv/";

		String fileName1 = new TimezoneTime().getCurrentDate();
		String fileName2 = "_Tip_Eligible_Partners";
		String fullFileName = filePath + fileName1 + fileName2 + ".csv";
		try {
			Reader reader = Files.newBufferedReader(Paths.get(fullFileName));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				// Accessing Values by Column Index
				if (csvRecord.get(0) != null) {
					String[] value = csvRecord.get(0).split("\\|");
					// [20180119, 7339216, Daniel, Kanmore, 00057A96, T,
					// 2017-10-23, Customer Champion I]
					String todaysDate = value[0];
					String empId = value[1]; // username
					String firstName = value[2];
					String lastName = value[3];
					String departmentId = value[4]; // ignore this fields
					String employeeStatus = value[5];
					String date = value[6];
					String jobTitle = value[7];

					User user = null;
					try {
						user = new UserManagementServiceBean(httpRequest, em).getUserByUserName(empId);
						logger.severe(user.toString());

					} catch (NoResultException e) {
						logger.severe("No result found for username" + empId);
					}

					if (user != null) {
						em.getTransaction().begin();
						HEBEmployeeExceptionalReport employeeExceptionalReport = new HEBEmployeeExceptionalReport()
								.insertHEBEmployeeExceptionalReport(todaysDate, employeeStatus, firstName, lastName,
										jobTitle, user.getStatus(), date, user.getId(), null, empId, null);

						if (user.getStatus().equals(employeeStatus)) {
							// do nothing
							// Records for Partners on this file with an
							// EMPL_STATUS matching the status in Nirvana XP,
							// will be ignored.
						} else {
							if (user.getStatus().equals("A")
									&& (employeeStatus.equals("P") || employeeStatus.equals("T")
											|| employeeStatus.equals("R") || employeeStatus.equals("D"))) {
								user.setStatus("I");
								employeeExceptionalReport.setStatus("I");
								user = em.merge(user);
								EmployeeMaster employeeMaster = getEmployeeMasterByUserId(user.getId(), em);
								if (employeeMaster != null) {
									employeeMaster.setStatus(employeeStatus);
									EmployeeMasterHistory employeeMasterHistory = new EmployeeMasterHistory()
											.employeeMasterHistoryObject(employeeMaster);
									employeeMaster = em.merge(employeeMaster);
									employeeMasterHistory = em.merge(employeeMasterHistory);
								}
							} else if (user.getStatus().equals("I") && (employeeStatus.equals("A"))) {
								// Records for Partners on this file with an
								// EMPL_STATUS of 'A', who have a status of
								// 'Inactive' in Nirvana XP will not load into
								// Nirvana XP, but will be written to an
								// exception report.
								employeeExceptionalReport.setStatus(user.getStatus());
								String errorMessage = "Records for Partners on this file with an EMPL_STATUS of 'A', who have a status of 'Inactive' in Nirvana XP ";
								employeeExceptionalReport.setErrorMessage(errorMessage);

							}

						}
						em.persist(employeeExceptionalReport);
						em.getTransaction().commit();
					} else {
						// create new users
						if ( employeeStatus.equals("A")) {
							em.getTransaction().begin();
							User u = new User();
							u.setUsername(empId);
							u.setFirstName(firstName);
							u.setLastName(lastName);
							u.setCreatedBy(createdBy);
							u.setUpdatedBy(createdBy);
							u.setStatus("A");
							// get roles
							Set<com.nirvanaxp.global.types.entities.UsersToRole> globalRolesSet = new HashSet<com.nirvanaxp.global.types.entities.UsersToRole>();
							int roleId = getRolesIdForGlobalDatabase(globalEM, accountId, "POS Operator");
							if (roleId > 0) {
								UsersToRole globalRoles = new UsersToRole(createdBy, roleId, createdBy, null);
								globalRoles.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								globalRoles.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

								// if current roles is added for user, then do
								// not
								// duplicate it at global level
								if (globalRolesSet != null && globalRolesSet.contains(globalRoles) == false) {
									globalRolesSet.add(globalRoles);
								}
							}

//							GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
//							UserManagementObj userManagementObj = globalUsermanagement.addUserToGlobalAndLocalDatabase(
//									httpRequest, globalEM, em, u, locationId, null, globalRolesSet,httpRequest,null);

							HEBEmployeeExceptionalReport employeeExceptionalReport = new HEBEmployeeExceptionalReport()
									.insertHEBEmployeeExceptionalReport(todaysDate, employeeStatus, firstName, lastName,
											jobTitle, "A", date, createdBy, null, empId, null);
							if (!em.getTransaction().isActive()) {
								em.getTransaction().begin();
							}
							// we are not doing this now as in file it is not
							// mentioned
							em.persist(employeeExceptionalReport);
							em.getTransaction().commit();

						}
					}
				}

			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.severe(e);
		}
		return result;

	}

	// @Override
	public EmployeeMaster getEmployeeMasterByUserId(String userId, EntityManager em) {
		EmployeeMaster result = null;
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<EmployeeMaster> criteria = builder.createQuery(EmployeeMaster.class);
			Root<EmployeeMaster> r = criteria.from(EmployeeMaster.class);
			TypedQuery<EmployeeMaster> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(EmployeeMaster_.userId), userId)));
			result = (EmployeeMaster) query.getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return result;
	}

	private int getRolesIdForGlobalDatabase(EntityManager globalEntityManager, int accoutId, String roleName) {
		try {
			CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> root = criteria.from(Role.class);
			TypedQuery<Role> query = globalEntityManager.createQuery(criteria.select(root).where(
					builder.equal(root.get(com.nirvanaxp.global.types.entities.Role_.roleName), roleName),
					builder.equal(root.get(com.nirvanaxp.global.types.entities.Role_.accountId), accoutId)));
			Role role = query.getSingleResult();
			return role.getId();
		} catch (NoResultException noResultException) {
			logger.info("no role found for the " + roleName);
			return 0;
		} catch (Exception e) {
			logger.severe(e, "Error getting user by id in global database: ", e.getMessage());
		}
		return 0;
	}

}
