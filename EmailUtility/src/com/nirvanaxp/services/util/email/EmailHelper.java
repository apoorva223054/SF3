package com.nirvanaxp.services.util.email;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.email.BusinessEmailSetting;
import com.nirvanaxp.types.entities.email.BusinessEmailSetting_;
import com.nirvanaxp.types.entities.email.EmailTemplate;
import com.nirvanaxp.types.entities.email.EmailTemplate_;
import com.nirvanaxp.types.entities.email.RolesToEmailTemplate;
import com.nirvanaxp.types.entities.email.RolesToEmailTemplate_;
import com.nirvanaxp.types.entities.email.SmtpConfig;
import com.nirvanaxp.types.entities.email.SmtpConfig_;
import com.nirvanaxp.types.entities.function.Function;
import com.nirvanaxp.types.entities.function.Function_;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.roles.RolesToFunction;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToRole;

// TODO: Auto-generated Javadoc
/**
 * The Class EmailHelper.
 */
public class EmailHelper
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(SendEmail.class.getName());

	/**
	 * Gets the SMTP config by location id.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @return the SMTP config by location id
	 */
	public SmtpConfig getSMTPConfigByLocationId(EntityManager em, String locationId)
	{


		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SmtpConfig> criteria = builder.createQuery(SmtpConfig.class);
		Root<SmtpConfig> r = criteria.from(SmtpConfig.class);
		TypedQuery<SmtpConfig> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SmtpConfig_.locationId), locationId), builder.notEqual(r.get(SmtpConfig_.status), "D")));

		SmtpConfig config = (SmtpConfig) query.getSingleResult();

		return config;
	}

	/**
	 * Gets the email template by location id.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @param operationName the operation name
	 * @return the email template by location id
	 */
	public EmailTemplate getEmailTemplateByLocationId(EntityManager em, String locationId, String operationName)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteria = builder.createQuery(EmailTemplate.class);
		Root<EmailTemplate> r = criteria.from(EmailTemplate.class);
		TypedQuery<EmailTemplate> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(EmailTemplate_.locationId), locationId),
				builder.equal(r.get(EmailTemplate_.operationName), operationName), builder.notEqual(r.get(EmailTemplate_.status), "D")));

		EmailTemplate emailTemplate = (EmailTemplate) query.getSingleResult();

		return emailTemplate;
	}

	/**
	 * Send email by email template id location id.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @param emailTemplateId the email template id
	 * @return true, if successful
	 */
	public boolean sendEmailByEmailTemplateIdLocationId(EntityManager em, String locationId, int emailTemplateId)
	{
		boolean result = false;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<BusinessEmailSetting> criteria = builder.createQuery(BusinessEmailSetting.class);
		Root<BusinessEmailSetting> r = criteria.from(BusinessEmailSetting.class);
		TypedQuery<BusinessEmailSetting> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(BusinessEmailSetting_.locationId), locationId),
				builder.equal(r.get(BusinessEmailSetting_.emailTemplateId), emailTemplateId), builder.notEqual(r.get(EmailTemplate_.status), "D")));

		BusinessEmailSetting businessEmailSetting = query.getSingleResult();
		if (businessEmailSetting != null)
		{
			result = true;
		}
		else
		{
			result = false;
		}
		return result;
	}

	/**
	 * Send email by email template id location id request order.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @param emailTemplateId the email template id
	 * @return the list
	 */
	public List<User> sendEmailByEmailTemplateIdLocationIdRequestOrder(EntityManager em, String locationId, int emailTemplateId)
	{
		List<User> users = new ArrayList<User>();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RolesToEmailTemplate> criteria = builder.createQuery(RolesToEmailTemplate.class);
		Root<RolesToEmailTemplate> r = criteria.from(RolesToEmailTemplate.class);
		TypedQuery<RolesToEmailTemplate> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RolesToEmailTemplate_.locationId), locationId),
				builder.equal(r.get(RolesToEmailTemplate_.emailTemplateId), emailTemplateId), builder.notEqual(r.get(RolesToEmailTemplate_.status), "D")));

		List<RolesToEmailTemplate> rolesToEmailTemplate = query.getResultList();
		if (rolesToEmailTemplate != null && rolesToEmailTemplate.size() > 0)
		{

			String roles = "";
			for (int i = 0; i < rolesToEmailTemplate.size(); i++)
			{
				if (i == rolesToEmailTemplate.size() - 1)
				{
					roles = roles + rolesToEmailTemplate.get(i).getRolesId();
				}
				else
				{
					roles = roles + rolesToEmailTemplate.get(i).getRolesId() + ",";
				}

			}
			String queryString = "select utr from UsersToRole utr where utr.rolesId in (" + roles + ")";
			TypedQuery<UsersToRole> query1 = em.createQuery(queryString, UsersToRole.class);
			List<UsersToRole> roleList = (query1).getResultList();

			String roleArray = "";
			for (int i = 0; i < roleList.size(); i++)
			{
				if (i == roleList.size() - 1)
				{
					roleArray = roleArray + roleList.get(i).getRolesId();
				}
				else
				{
					roleArray = roleArray + roleList.get(i).getRolesId() + ",";
				}

			}

			String queryString2 = "select utr from UsersToRole utr where utr.rolesId in (" + roleArray + ")";
			TypedQuery<UsersToRole> query4 = em.createQuery(queryString2, UsersToRole.class);
			List<UsersToRole> usersList = (query4).getResultList();

			String usersArray = "";
			for (int i = 0; i < usersList.size(); i++)
			{
				if (i == usersList.size() - 1)
				{
					usersArray = usersArray + usersList.get(i).getUsersId();
				}
				else
				{
					usersArray = usersArray + usersList.get(i).getUsersId() + ",";
				}

			}

			String queryString1 = "select u from User u where u.status != 'D' and  u.id in (" + usersArray + ")";
			TypedQuery<User> query2 = em.createQuery(queryString1, User.class);
			users = (query2).getResultList();

		}
		return users;
	}

	/**
	 * Send email by email template id location id EOD settlement.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @param emailTemplateId the email template id
	 * @return the list
	 */
	public List<User> sendEmailByEmailTemplateIdLocationIdEODSettlement(EntityManager em, String locationId, int emailTemplateId)
	{
		List<User> users = new ArrayList<User>();
		// TODO :- modlular coding reqired and handling exception

		// fetch function in which email template is eod
		
		Function function = null;
		try
		{
			CriteriaBuilder builder1 = em.getCriteriaBuilder();
			CriteriaQuery<Function> criteria1 = builder1.createQuery(Function.class);
			Root<Function> r1 = criteria1.from(Function.class);
			TypedQuery<Function> query3 = em.createQuery(criteria1.select(r1).where(builder1.equal(r1.get(Function_.name), "EOD Settlement Email"), builder1.notEqual(r1.get(Function_.status), "D"),
					builder1.notEqual(r1.get(Function_.status), "I")));
			function = query3.getSingleResult();
			 
		}
		catch (Exception e)
		{
			logger.severe("No Entity Find For Function EOD Settlement Email");
		}
		// now we are finding roles associated to that function
		// now after that we will fetch all user whose role belongs to function
		if (function !=null)
		{
			List<RolesToFunction> roleList = new ArrayList<RolesToFunction>();
			try
			{
				String queryString = "select utr from RolesToFunction utr "
						+ " where utr.functionsId = ? and utr.status not in ('D', 'I')"
								+ " and  rolesId in (select id from Role where locationsId = "+locationId+")";
				
				TypedQuery<RolesToFunction> query1 = em.createQuery(queryString, RolesToFunction.class).setParameter(1, function.getId());
				roleList = query1.getResultList();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.severe("No Entity Find For Roles To Function EOD Settlement Email");
			}

			
			
			
			String roleArray = "";
			for (int i = 0; i < roleList.size(); i++)
			{
				if (i == roleList.size() - 1)
				{
					roleArray +=  roleList.get(i).getRolesId();
				}
				else
				{
					roleArray +=  roleList.get(i).getRolesId() + ",";
				}

			}

			if (roleArray.length()>0)
			{
				List<UsersToRole> usersList = new ArrayList<UsersToRole>();
				try
				{
					String queryString2 = "select utr from UsersToRole utr where utr.rolesId in ('" + roleArray + "') and utr.status != 'D' and utr.status != 'I'";
					TypedQuery<UsersToRole> query4 = em.createQuery(queryString2, UsersToRole.class);
					usersList = query4.getResultList();
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					logger.severe("No Entity Find For Users To Role EOD Settlement Email");
				}

				String usersArray = "";
				for (int i = 0; i < usersList.size(); i++)
				{
					if (i == usersList.size() - 1)
					{
						usersArray +=  usersList.get(i).getUsersId();
					}
					else
					{
						usersArray +=  usersList.get(i).getUsersId() + ",";
					}

				}

				if (usersArray.length()>0)
				{
					String queryString1 = "select u from User u where u.status != 'D' and u.status != 'I' and u.id in ('" + usersArray + "')";
					TypedQuery<User> query2 = em.createQuery(queryString1, User.class);
					users = (query2).getResultList();
				}

			}

		}

		return users;
	}

	/**
	 * Gets the time for email.
	 *
	 * @param reservation the reservation
	 * @return the time for email
	 * @throws ParseException the parse exception
	 */
	public String getTimeForEmail(Reservation reservation) throws ParseException
	{

		String timeString = reservation.getTime();

		SimpleDateFormat formatterForFormat = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat formatterForParse = new SimpleDateFormat("HH:mm:ss");
		Date date = formatterForParse.parse(timeString);
		timeString = formatterForFormat.format(date);

		return timeString;
	}

	/**
	 * Gets the date for email.
	 *
	 * @param reservation the reservation
	 * @return the date for email
	 * @throws ParseException the parse exception
	 */
	public String getDateForEmail(Reservation reservation) throws ParseException
	{
		String dateString = reservation.getDate();

		SimpleDateFormat formatterForFormat = new SimpleDateFormat("E, MMM dd yyyy");
		SimpleDateFormat formatterForParse = new SimpleDateFormat("yyyy-MM-dd");
		Date date = formatterForParse.parse(dateString);
		dateString = formatterForFormat.format(date);

		return dateString;
	}
	
	public List<User> sendEmailByEmailTemplateIdLocationIdEODTipSettlement(EntityManager em, String locationId, int emailTemplateId)
	{
		List<User> users = new ArrayList<User>();
		// TODO :- modlular coding reqired and handling exception

		// fetch function in which email template is eod
	
		Function function = null;
		try
		{
			CriteriaBuilder builder1 = em.getCriteriaBuilder();
			CriteriaQuery<Function> criteria1 = builder1.createQuery(Function.class);
			Root<Function> r1 = criteria1.from(Function.class);
			TypedQuery<Function> query3 = em.createQuery(criteria1.select(r1).where(builder1.equal(r1.get(Function_.name), "Tip Settlement"), builder1.notEqual(r1.get(Function_.status), "D"),
					builder1.notEqual(r1.get(Function_.status), "I")));
			function = query3.getSingleResult();
			 
		}
		catch (Exception e)
		{
			logger.severe("No Entity Find For Function EOD Tip Settlement Email");
		}
		// now we are finding roles associated to that function
		// now after that we will fetch all user whose role belongs to function
		if (function != null)
		{
			List<RolesToFunction> roleList = new ArrayList<RolesToFunction>();
			try
			{
				String queryString = "select utr from RolesToFunction utr "
						+ " where utr.functionsId =? and utr.status not in ('D', 'I')"
								+ " and   utr.rolesId in (select id from Role where locationsId = ?)";
				
				TypedQuery<RolesToFunction> query1 = em.createQuery(queryString, RolesToFunction.class);
				query1.setParameter(1, function.getId()).setParameter(2, locationId);
				roleList = query1.getResultList();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.severe("No Entity Find For Roles To Function EOD Tip Settlement Email"+e);
			}

			
				
			String roleArray = "";
			for (int i = 0; i < roleList.size(); i++)
			{
				if (i == roleList.size() - 1)
				{
					roleArray = roleArray + roleList.get(i).getRolesId();
				}
				else
				{
					roleArray = roleArray + roleList.get(i).getRolesId() + ",";
				}

			}
			if (!roleArray.isEmpty())
			{
				List<UsersToRole> usersList = new ArrayList<UsersToRole>();
				try
				{
					String queryString2 = "select utr from UsersToRole utr where utr.rolesId in ('"+roleArray+"') and utr.status != 'D' and utr.status != 'I'";
					TypedQuery<UsersToRole> query4 = em.createQuery(queryString2, UsersToRole.class);
					usersList = query4.getResultList();
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					logger.severe("No Entity Find For Users To Role EOD Tip Settlement Email");
				}

				String usersArray = "";
				for (int i = 0; i < usersList.size(); i++)
				{
					if (i == usersList.size() - 1)
					{
						usersArray = usersArray + usersList.get(i).getUsersId();
					}
					else
					{
						usersArray = usersArray + usersList.get(i).getUsersId() + ",";
					}

				}
				if (!usersArray.isEmpty())
				{
					String queryString1 = "select u from User u where u.status != 'D' and u.status != 'I' and u.id in ("+usersArray+")";
					TypedQuery<User> query2 = em.createQuery(queryString1, User.class);
					users = (query2).getResultList();
				}

			}

		}

		return users;
	}

	public List<User> sendEmailByFunctionByLocationIdWithQuery(EntityManager em,String locationId,String functionName)
	{
		List<User> users = new ArrayList<User>();
			try
			{
				String queryString = "select  distinct u.id from roles_to_functions rtf "
						+ " join functions f on f.id=rtf.functions_id "
						+ " join users_to_roles utr on utr.roles_id=rtf.roles_id "
						+ " join roles r on r.id= utr.roles_id "
						+ " join users u on u.id = utr.users_id where  f.name='"+functionName+"' "
								+ " and r.locations_id= ? and rtf.status not in ('D','I')"
								+ " and utr.status not in ('D','I') ";
				
				List<Object> result = em.createNativeQuery(queryString).setParameter(1, locationId).getResultList();
				for(Object object:result){
					String userId = (String)object;
					users.add((User) new CommonMethods().getObjectById("User", em,User.class, userId));
					
					
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.severe(e);
			}
		return users;
	}
}
