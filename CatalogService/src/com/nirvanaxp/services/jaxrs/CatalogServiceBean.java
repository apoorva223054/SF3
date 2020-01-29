/**
 * Copyright (c) 2012 - 2018 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.relationalentity.EntityRelationshipManager;
import com.nirvanaxp.common.utils.relationalentity.helper.CategoryRelationsHelper;
import com.nirvanaxp.common.utils.relationalentity.helper.ItemRelationsHelper;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.packets.CatalogPacket;
import com.nirvanaxp.services.jaxrs.packets.CategoryIdPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.category.Category_;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem_;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemGroup;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.custom.CatalogDisplayService;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.printers.Printer;

class CatalogServiceBean {

	private static final NirvanaLogger logger = new NirvanaLogger(CatalogServiceBean.class.getName());

	Category add(EntityManager em, CatalogPacket catalogPacket) throws Exception {

		addUpdateCategory(em, catalogPacket);
		Category category = catalogPacket.getCategory();
		em.refresh(category);
		return category;

	}

	private Category addUpdateCategory(EntityManager em, CatalogPacket catalogPacket) throws Exception {
		// we make this null, so that this relation does not interfere and
		// throws exception while adding

		Category category = catalogPacket.getCategory();

		logger.severe("category===================================================================="+category);
		if (category.getGlobalCategoryId() == null) {
			Category local = (Category) new CommonMethods().getObjectById("Category", em, Category.class,
					category.getId());
			if (local != null) {
				category.setGlobalCategoryId(local.getGlobalCategoryId());
				category.setSortSequence(local.getSortSequence());
			}
		}

		if(category.getCategoryId()!=null && category.getCategoryId().equals("null")){
			category.setCategoryId(null);
		}
		category.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		// TODO uzma - handle add update in category service
		if (category.getId() == null || category.getId().length() == 0) {
			category.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			category.setId(new StoreForwardUtility().generateUUID());
			em.persist(category);
		} else {
			category = em.merge(category);
		}

		em.getTransaction().commit();
		em.getTransaction().begin();
		CategoryRelationsHelper categoryRelationsHelper = new CategoryRelationsHelper();
		categoryRelationsHelper.setShouldEliminateDStatus(true);

		em.getTransaction().commit();
		em.getTransaction().begin();
		List<CategoryToPrinter> categoryToPrintersListCurrent = categoryRelationsHelper
				.getCategoryToPrinterAlongWithDStatus(category.getId(), em);
		em.getTransaction().commit();
		em.getTransaction().begin();
		List<CategoryToDiscount> categoryToDiscountsListCurrent = categoryRelationsHelper
				.getCategoryToDiscountAlongWithDStatus(category.getId(), em);
		em.getTransaction().commit();
		em.getTransaction().begin();
		List<CategoryToPrinter> unmodifiedCategoryToPrintersListCurrent = new ArrayList<CategoryToPrinter>();
		getUnmodifiedCopyForArrayListOfPrinter(unmodifiedCategoryToPrintersListCurrent, categoryToPrintersListCurrent);
		em.getTransaction().commit();
		em.getTransaction().begin();
		List<CategoryToDiscount> unmodifiedCategoryToDiscountsListCurrent = new ArrayList<CategoryToDiscount>();
		getUnmodifiedCopyForArrayListOfDiscounts(unmodifiedCategoryToDiscountsListCurrent,
				categoryToDiscountsListCurrent);
		em.getTransaction().commit();
		em.getTransaction().begin();
		EntityRelationshipManager manager = new EntityRelationshipManager();
		// naman
		manager.manageRelations(em, category, categoryToPrintersListCurrent, catalogPacket.getPrinterList(),
				CategoryToPrinter.class);
		em.getTransaction().commit();
		em.getTransaction().begin();
		manager.manageRelations(em, category, categoryToDiscountsListCurrent, catalogPacket.getDiscountsList(),
				CategoryToDiscount.class);
		em.getTransaction().commit();
		em.getTransaction().begin();
		manageRelationsOnSubCategory(em, category, unmodifiedCategoryToPrintersListCurrent,
				catalogPacket.getPrinterList(), unmodifiedCategoryToDiscountsListCurrent,
				catalogPacket.getDiscountsList());
		em.getTransaction().commit();
		em.getTransaction().begin();
		manageRelationForSubCategoryForIsRealTimeUpdateNeeded(em, category, categoryRelationsHelper,
				category.getIsUpdateOverridden());
		em.getTransaction().commit();
		em.getTransaction().begin();
		if (catalogPacket.getDiscountsList() != null && catalogPacket.getDiscountsList().size() > 0) {
			// delete previous relation shinp of item to discount for main cat
			manageRelationShipForDeleteCategoryToItemDiscount(em, category);
			em.getTransaction().commit();
			em.getTransaction().begin();
			List<Category> subCategoriesList = categoryRelationsHelper.getSubCategoriesForCategoryId(category.getId(),
					em);

			// delete previous relation shinp of item to discount for main sub
			// cat
			if (subCategoriesList != null && subCategoriesList.size() > 0) {
				for (Category suCategory : subCategoriesList) {

					// Sub cat delete relationship
					manageRelationShipForDeleteCategoryToItemDiscount(em, suCategory);
					em.getTransaction().commit();
					em.getTransaction().begin();
					// Sub Sub cat delete relationship
					List<Category> subSubCategoriesList = categoryRelationsHelper
							.getSubCategoriesForCategoryId(suCategory.getId(), em);
					em.getTransaction().commit();
					em.getTransaction().begin();
					if (subSubCategoriesList != null && subSubCategoriesList.size() > 0) {
						for (Category subSubCategory : subSubCategoriesList) {

							manageRelationShipForDeleteCategoryToItemDiscount(em, subSubCategory);
							em.getTransaction().commit();
							em.getTransaction().begin();
						}
					}
				}
			}

			manageRelationShipWithCategoryToItemDiscount(em, category, catalogPacket);
			em.getTransaction().commit();
			em.getTransaction().begin();
			// subCategoriesList = categoryRelationsHelper
			// .getSubCategoriesForCategoryId(category.getId(), em);

			if (subCategoriesList != null && subCategoriesList.size() > 0) {
				for (Category suCategory : subCategoriesList) {

					// sub cat relationship
					manageRelationShipWithCategoryToItemDiscount(em, suCategory, catalogPacket);
					em.getTransaction().commit();
					em.getTransaction().begin();
					// sub sub cat relationship
					List<Category> subSubCategoriesList = categoryRelationsHelper
							.getSubCategoriesForCategoryId(suCategory.getId(), em);
					if (subCategoriesList != null && subCategoriesList.size() > 0) {
						for (Category subSubCategory : subSubCategoriesList) {

							manageRelationShipWithCategoryToItemDiscount(em, subSubCategory, catalogPacket);
							em.getTransaction().commit();
							em.getTransaction().begin();
						}
					}

				}
			}
		}

		if (catalogPacket.getPrinterList() != null && catalogPacket.getPrinterList().size() > 0) {

			// delete previous relation shinp of item to printer for main cat
			manageRelationShipForDeleteCategoryToItemPrinter(em, category);
			em.getTransaction().commit();
			em.getTransaction().begin();
			List<Category> subCategoriesList = categoryRelationsHelper.getSubCategoriesForCategoryId(category.getId(),
					em);

			// delete previous relation shinp of item to discount for main sub
			// cat
			if (subCategoriesList != null && subCategoriesList.size() > 0) {
				for (Category suCategory : subCategoriesList) {

					// Sub cat delete relationship
					manageRelationShipForDeleteCategoryToItemPrinter(em, suCategory);
					em.getTransaction().commit();
					em.getTransaction().begin();
					// Sub Sub cat delete relationship
					List<Category> subSubCategoriesList = categoryRelationsHelper
							.getSubCategoriesForCategoryId(suCategory.getId(), em);
					if (subSubCategoriesList != null && subSubCategoriesList.size() > 0) {
						for (Category subSubCategory : subSubCategoriesList) {

							manageRelationShipForDeleteCategoryToItemPrinter(em, subSubCategory);
							em.getTransaction().commit();
							em.getTransaction().begin();
						}
					}
				}
			}

			manageRelationShipWithCategoryToItemPrinter(em, category, catalogPacket);
			em.getTransaction().commit();
			em.getTransaction().begin();
			if (subCategoriesList != null && subCategoriesList.size() > 0) {
				for (Category suCategory : subCategoriesList) {

					// sub cat relationship
					manageRelationShipWithCategoryToItemPrinter(em, suCategory, catalogPacket);
					em.getTransaction().commit();
					em.getTransaction().begin();

					// sub sub cat relationship
					List<Category> subSubCategoriesList = categoryRelationsHelper
							.getSubCategoriesForCategoryId(suCategory.getId(), em);
					if (subCategoriesList != null && subCategoriesList.size() > 0) {
						for (Category subSubCategory : subSubCategoriesList) {

							manageRelationShipWithCategoryToItemPrinter(em, subSubCategory, catalogPacket);
							em.getTransaction().commit();
							em.getTransaction().begin();
						}
					}

				}
			}
		}

		// relationship with category to item group
		if (catalogPacket.getCategory().getItemGroupId() != null) {

			String catItemGroupId = catalogPacket.getCategory().getItemGroupId();
			List<Category> subCategoriesList = categoryRelationsHelper.getSubCategoriesForCategoryId(category.getId(),
					em);

			manageRelationShipForItemGroup(em, category, catItemGroupId);
			em.getTransaction().commit();
			em.getTransaction().begin();
			if (subCategoriesList != null && subCategoriesList.size() > 0) {
				for (Category suCategory : subCategoriesList) {

					// sub cat relationship
					suCategory.setItemGroupId(catItemGroupId);
					suCategory = em.merge(suCategory);
					em.getTransaction().commit();
					em.getTransaction().begin();
					manageRelationShipForItemGroup(em, suCategory, catItemGroupId);
					em.getTransaction().commit();
					em.getTransaction().begin();
					// sub sub cat relationship
					List<Category> subSubCategoriesList = categoryRelationsHelper
							.getSubCategoriesForCategoryId(suCategory.getId(), em);
					if (subCategoriesList != null && subCategoriesList.size() > 0) {
						for (Category subSubCategory : subSubCategoriesList) {

							subSubCategory.setItemGroupId(catItemGroupId);
							subSubCategory = em.merge(subSubCategory);
							em.getTransaction().commit();
							em.getTransaction().begin();
							manageRelationShipForItemGroup(em, subSubCategory, catItemGroupId);
							em.getTransaction().commit();
							em.getTransaction().begin();
						}
					}

				}
			}
		}

		if (category.getIsinventoryAccrualOverriden() == 1) {
			manageRelationForSubCategoryForInventoryAccrual(em, category, categoryRelationsHelper,
					category.getIsinventoryAccrualOverriden());
			em.getTransaction().commit();
			em.getTransaction().begin();
		}

		return category;

	}

	private void manageRelationShipForDeleteCategoryToItemDiscount(EntityManager em, Category category) {
		List<Item> itemList = new ArrayList<Item>();

		try {

			TypedQuery<Item> query = em
					.createQuery("SELECT i FROM Item i, CategoryItem ci WHERE ci.categoryId =? AND ci.itemsId = i.id "
							+ "and ci.status !='D' ", Item.class)
					.setParameter(1, category.getId());
			itemList = query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
			logger.severe("No CategoryItem Found for Category id" + category.getId());
		}

		if (itemList != null && itemList.size() > 0) {
			for (Item item : itemList) {
				if (item != null) {

					try {

						TypedQuery<ItemsToDiscount> itemsToDiscountQuery = em
								.createQuery("SELECT i FROM ItemsToDiscount i WHERE i.itemsId = ? and i.status != 'D'",
										ItemsToDiscount.class)
								.setParameter(1, item.getId());
						List<ItemsToDiscount> itemToDiscount = itemsToDiscountQuery.getResultList();

						if (itemToDiscount != null) {
							for (ItemsToDiscount discount : itemToDiscount) {
								discount.setStatus("D");
								discount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								discount = em.merge(discount);
							}

						}
					} catch (NoResultException e) {
						logger.severe(e);
						logger.severe("No ItemsToDiscount For Delete Found for Item id " + item.getId());
					}

				}
			}
		}
	}

	private void manageRelationShipForDeleteCategoryToItemPrinter(EntityManager em, Category category) {
		List<Item> itemList = new ArrayList<Item>();
		try {

			TypedQuery<Item> query = em
					.createQuery("SELECT i FROM Item i, CategoryItem ci WHERE ci.categoryId =? AND ci.itemsId = i.id "
							+ "and ci.status !='D' ", Item.class)
					.setParameter(1, category.getId());
			itemList = query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
			logger.severe("No CategoryItem Found for Category id" + category.getId());
		}

		if (itemList != null && itemList.size() > 0) {
			for (Item item : itemList) {
				if (item != null) {

					try {

						TypedQuery<ItemsToPrinter> itemsToPrinterQuery = em
								.createQuery("SELECT i FROM ItemsToPrinter i WHERE i.itemsId = ? and i.status != 'D'",
										ItemsToPrinter.class)
								.setParameter(1, item.getId());
						List<ItemsToPrinter> itemToPrinterList = itemsToPrinterQuery.getResultList();

						if (itemToPrinterList != null) {
							for (ItemsToPrinter printer : itemToPrinterList) {

								printer.setStatus("D");
								printer.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								printer = em.merge(printer);
							}

						}
					} catch (NoResultException e) {
						logger.severe(e);
						logger.severe("No ItemsToPrinter For Delete Found for Item id " + item.getId());
					}

				}
			}
		}
	}

	private void manageRelationShipWithCategoryToItemDiscount(EntityManager em, Category category,
			CatalogPacket myPacket) {
		List<Item> itemList = new ArrayList<Item>();
		try {

			TypedQuery<Item> query = em
					.createQuery("SELECT i FROM Item i, CategoryItem ci WHERE ci.categoryId =? AND ci.itemsId = i.id "
							+ "and ci.status !='D' ", Item.class)
					.setParameter(1, category.getId());
			itemList = query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
			logger.severe("No CategoryItem Found for Category id" + category.getId());
		}

		if (itemList != null && itemList.size() > 0) {
			for (Item item : itemList) {
				if (item != null) {

					for (Discount discount : myPacket.getDiscountsList()) {
						ItemsToDiscount newItemToDiscount = new ItemsToDiscount();
						newItemToDiscount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						newItemToDiscount.setUpdatedBy(category.getUpdatedBy());
						newItemToDiscount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						newItemToDiscount.setCreatedBy(category.getCreatedBy());
						newItemToDiscount.setStatus("A");
						newItemToDiscount.setBaseRelation(null);
						newItemToDiscount.setBaseToObjectRelation(null);
						newItemToDiscount.setItemsId(item.getId());
						newItemToDiscount.setDiscountsId(discount.getId());
						newItemToDiscount.setId(new StoreForwardUtility().generateUUID());
						em.persist(newItemToDiscount);
					}

				}
			}
		}
	}

	private void manageRelationShipWithCategoryToItemPrinter(EntityManager em, Category category,
			CatalogPacket myPacket) {
		List<Item> itemList = new ArrayList<Item>();
		try {

			TypedQuery<Item> query = em
					.createQuery("SELECT i FROM Item i, CategoryItem ci WHERE ci.categoryId =? AND ci.itemsId = i.id "
							+ "and ci.status !='D' ", Item.class)
					.setParameter(1, category.getId());
			itemList = query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
			logger.severe("No CategoryItem Found for Category id" + category.getId());
		}

		if (itemList != null && itemList.size() > 0) {
			for (Item item : itemList) {
				if (item != null) {

					for (Printer printer : myPacket.getPrinterList()) {
						ItemsToPrinter newItemToPrinter = new ItemsToPrinter();
						newItemToPrinter.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						newItemToPrinter.setUpdatedBy(category.getUpdatedBy());
						newItemToPrinter.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						newItemToPrinter.setCreatedBy(category.getCreatedBy());
						newItemToPrinter.setStatus("A");
						newItemToPrinter.setBaseRelation(null);
						newItemToPrinter.setBaseToObjectRelation(null);
						newItemToPrinter.setItemsId(item.getId());
						newItemToPrinter.setPrintersId(printer.getId());
						em.persist(newItemToPrinter);
					}

				}
			}
		}
	}

	private void manageRelationForSubCategoryForInventoryAccrual(EntityManager em, Category category,
			CategoryRelationsHelper categoryRelationsHelper, int isinventoryAccrualOverriden) {
		List<Category> subCategoriesList = categoryRelationsHelper.getSubCategoriesForCategoryId(category.getId(), em);
		if (subCategoriesList != null && subCategoriesList.size() > 0) {
			for (Category suCategory : subCategoriesList) {
				if (suCategory != null && suCategory.getInventoryAccrual() != category.getInventoryAccrual()) {
					suCategory.setIsinventoryAccrualOverriden(isinventoryAccrualOverriden);
					suCategory.setInventoryAccrual(category.getInventoryAccrual());
					em.merge(suCategory);

					manageRelationForSubCategoryForInventoryAccrual(em, suCategory, categoryRelationsHelper,
							isinventoryAccrualOverriden);

				}
			}
		}
		updateInvetoryAccrualValue(em, category, category.getInventoryAccrual());
	}

	private void updateInvetoryAccrualValue(EntityManager em, Category category, int inventoryAccrual) {
		TypedQuery<Item> query = em
				.createQuery("SELECT i FROM Item i, CategoryItem ci WHERE ci.categoryId = ? AND ci.itemsId = i.id",
						Item.class)
				.setParameter(1, category.getId());
		List<Item> itemList = query.getResultList();
		if (itemList != null && itemList.size() > 0) {
			for (Item item : itemList) {
				if (item != null && item.getInventoryAccrual() != category.getInventoryAccrual()) {
					item.setInventoryAccrual(category.getInventoryAccrual());
					em.merge(item);

				}
			}
		}

	}

	private void manageRelationsOnSubCategory(EntityManager em, Category category,
			List<CategoryToPrinter> categoryToPrintersListPrev, List<Printer> printerList,
			List<CategoryToDiscount> categoryToDiscountsListPrev, List<Discount> discountsList) {

		CategoryRelationsHelper categoryRelationsHelper = new CategoryRelationsHelper();

		manageRelationForSubCategoryForPrinter(em, category, categoryRelationsHelper, categoryToPrintersListPrev,
				printerList, true);

		manageRelationForSubCategoryForDiscounts(em, category, categoryRelationsHelper, categoryToDiscountsListPrev,
				discountsList, true);

	}

	private void manageRelationForSubCategoryForIsRealTimeUpdateNeeded(EntityManager em, Category category,
			CategoryRelationsHelper categoryRelationsHelper, int isUpdateOverriden) {

		List<Category> subCategoriesList = categoryRelationsHelper.getSubCategoriesForCategoryId(category.getId(), em);

		if (subCategoriesList != null && subCategoriesList.size() > 0) {

			for (Category suCategory : subCategoriesList) {

				if (suCategory != null
						&& suCategory.getIsRealTimeUpdateNeeded() != category.getIsRealTimeUpdateNeeded()) {
					suCategory.setIsRealTimeUpdateNeeded(category.getIsRealTimeUpdateNeeded());
					em.merge(suCategory);

					manageRelationForSubCategoryForIsRealTimeUpdateNeeded(em, suCategory, categoryRelationsHelper,
							isUpdateOverriden);
				}
			}
		}

		if (isUpdateOverriden == 1) {
			updateItemRealTimeUpdateValue(em, category, category.getIsRealTimeUpdateNeeded());
		}

	}

	private void updateItemRealTimeUpdateValue(EntityManager em, Category category, int isRealTimeUpdateNeeded) {

		TypedQuery<Item> query = em
				.createQuery("SELECT i FROM Item i, CategoryItem ci WHERE ci.categoryId =? AND ci.itemsId = i.id",
						Item.class)
				.setParameter(1, category.getId());
		List<Item> itemList = query.getResultList();
		if (itemList != null && itemList.size() > 0) {
			for (Item item : itemList) {
				if (item != null && item.getIsRealTimeUpdateNeeded() != category.getIsRealTimeUpdateNeeded()) {
					item.setIsRealTimeUpdateNeeded(category.getIsRealTimeUpdateNeeded());

					em.merge(item);

				}
			}
		}

	}

	private void getUnmodifiedCopyForArrayListOfPrinter(List<CategoryToPrinter> unmodifiedCategoryToPrintersListCurrent,
			List<CategoryToPrinter> categoryToPrintersListCurrent) {

		if (categoryToPrintersListCurrent != null) {
			for (CategoryToPrinter categoryToPrinter : categoryToPrintersListCurrent) {
				CategoryToPrinter newCategoryToPrinter = new CategoryToPrinter();
				newCategoryToPrinter.setCategoryId(categoryToPrinter.getCategoryId());
				newCategoryToPrinter.setPrintersId(categoryToPrinter.getPrintersId());
				newCategoryToPrinter.setCreated(categoryToPrinter.getCreated());
				newCategoryToPrinter.setUpdated(categoryToPrinter.getUpdated());
				newCategoryToPrinter.setUpdatedBy(categoryToPrinter.getUpdatedBy());
				newCategoryToPrinter.setCreatedBy(categoryToPrinter.getCreatedBy());
				unmodifiedCategoryToPrintersListCurrent.add(newCategoryToPrinter);
			}
		}
	}

	private void getUnmodifiedCopyForArrayListOfDiscounts(
			List<CategoryToDiscount> unmodifiedCategoryToDiscountsListCurrent,
			List<CategoryToDiscount> categoryToDiscountsListCurrent) {

		if (categoryToDiscountsListCurrent != null) {
			for (CategoryToDiscount categoryToDiscounts : categoryToDiscountsListCurrent) {
				CategoryToDiscount newCategoryToDiscounts = new CategoryToDiscount();
				newCategoryToDiscounts.setCategoryId(categoryToDiscounts.getCategoryId());
				newCategoryToDiscounts.setDiscountsId(categoryToDiscounts.getDiscountsId());
				newCategoryToDiscounts.setCreated(categoryToDiscounts.getCreated());
				newCategoryToDiscounts.setUpdated(categoryToDiscounts.getUpdated());
				newCategoryToDiscounts.setUpdatedBy(categoryToDiscounts.getUpdatedBy());
				newCategoryToDiscounts.setCreatedBy(categoryToDiscounts.getCreatedBy());
				unmodifiedCategoryToDiscountsListCurrent.add(newCategoryToDiscounts);
			}
		}
	}

	private void manageRelationForSubCategoryForPrinter(EntityManager em, Category category,
			CategoryRelationsHelper categoryRelationsHelper, List<CategoryToPrinter> categoryToPrintersListPrev,
			List<Printer> printerList, boolean shouldManageSubcategories) {

		List<Category> subCategoriesList = categoryRelationsHelper.getSubCategoriesForCategoryId(category.getId(), em);
		List<CategoryToPrinter> categoryToPrintersListCurrent = categoryRelationsHelper
				.getCategoryToPrinterAlongWithDStatus(category.getId(), em);

		if (subCategoriesList != null && subCategoriesList.size() > 0) {

			List<String> subCategoriesIdsList = new ArrayList<String>();
			for (Category suCategory : subCategoriesList) {
				subCategoriesIdsList.add(suCategory.getId());
			}

			// get category to printer
			List<CategoryToPrinter> subCategoryToPrinter = categoryRelationsHelper
					.getCategoryToPrintersForCategories(subCategoriesIdsList, em);
			if (subCategoryToPrinter != null && subCategoryToPrinter.size() > 0) {

				// some categories have printers already applied, we must
				// change printer of those that had default
				getCategoryToPrinterForCategoryFromList(subCategoryToPrinter, subCategoriesList);
				for (Category subCategory : subCategoriesList) {

					// check if this sub category has been overridden or not
					if (hasCategoryToPrinterBeenOverridden(categoryToPrintersListPrev,
							subCategory.getCategoryToPrinters()) == false) {

						// manage the relations for this one now
						List<CategoryToPrinter> unmodifiedCategoryToPrintersListCurrent = new ArrayList<CategoryToPrinter>();

						if (shouldManageSubcategories) {
							getUnmodifiedCopyForArrayListOfPrinter(unmodifiedCategoryToPrintersListCurrent,
									subCategory.getCategoryToPrinters());
						}
						// naman
						new EntityRelationshipManager().manageRelation(em, subCategory,
								subCategory.getCategoryToPrinters(), printerList, CategoryToPrinter.class);

						if (shouldManageSubcategories) {

							// pass right sun sun category unmodified

							/*
							 * if(subCategory.getCategoryId() == 0) {
							 * manageRelationForSubCategoryForPrinter(em,
							 * subCategory, categoryRelationsHelper,
							 * unmodifiedCategoryToPrintersListCurrent,
							 * printerList, false); }else {
							 */
							manageRelationForSubCategoryForPrinter(em, subCategory, categoryRelationsHelper,
									unmodifiedCategoryToPrintersListCurrent, printerList, true);
							// }

						}
					}
				}

			} else {
				// no discount is yet applied on any sub-category, replicate
				// the printer at sub category level
				if (categoryToPrintersListCurrent != null) {

					for (Category subCategory : subCategoriesList) {
						// naman
						new EntityRelationshipManager().manageRelations(em, subCategory,
								subCategory.getCategoryToPrinters(), printerList, CategoryToPrinter.class);

						if (shouldManageSubcategories) {
							manageRelationForSubCategoryForPrinter(em, subCategory, categoryRelationsHelper,
									subCategory.getCategoryToPrinters(), printerList, false);
						}

					}

				}

			}
		}
	}

	private void manageRelationForSubCategoryForDiscounts(EntityManager em, Category category,
			CategoryRelationsHelper categoryRelationsHelper, List<CategoryToDiscount> categoryToDiscountsListPrev,
			List<Discount> discountsList, boolean shouldManageSubcategories) {

		List<Category> subCategoriesList = categoryRelationsHelper.getSubCategoriesForCategoryId(category.getId(), em);

		List<CategoryToDiscount> categoryToDiscountsListCurrent = categoryRelationsHelper
				.getCategoryToDiscountAlongWithDStatus(category.getId(), em);

		if (subCategoriesList != null && subCategoriesList.size() > 0) {

			List<String> subCategoriesIdsList = new ArrayList<String>();
			for (Category suCategory : subCategoriesList) {
				subCategoriesIdsList.add(suCategory.getId());
			}

			// get category to printer
			List<CategoryToDiscount> subCategoryToDiscounts = categoryRelationsHelper
					.getCategoryToDiscountsForCategories(subCategoriesIdsList, em);
			if (subCategoryToDiscounts != null && subCategoryToDiscounts.size() > 0) {

				// some categories have printers already applied, we must
				// change printer of those that had default
				getCategoryToDiscountForCategoryFromList(subCategoryToDiscounts, subCategoriesList);
				for (Category subCategory : subCategoriesList) {

					// check if this sub category has been overridden or not
					if (hasCategoryToDiscountsBeenOverridden(categoryToDiscountsListPrev,
							subCategory.getCategoryToDiscounts()) == false) {
						// manage the relations for this one now
						List<CategoryToDiscount> unmodifiedDiscountsToDiscountCurrent = new ArrayList<CategoryToDiscount>();

						if (shouldManageSubcategories) {
							getUnmodifiedCopyForArrayListOfDiscounts(unmodifiedDiscountsToDiscountCurrent,
									subCategory.getCategoryToDiscounts());
						}

						new EntityRelationshipManager().manageRelations(em, subCategory,
								subCategory.getCategoryToDiscounts(), discountsList, CategoryToDiscount.class);

						if (shouldManageSubcategories) {

							// pass right sun sun category unmodified
							/*
							 * if(subCategory.getCategoryId() == 0) {
							 * manageRelationForSubCategoryForDiscounts(em,
							 * subCategory, categoryRelationsHelper,
							 * unmodifiedDiscountsToDiscountCurrent,
							 * discountsList, false); }else {
							 */
							manageRelationForSubCategoryForDiscounts(em, subCategory, categoryRelationsHelper,
									unmodifiedDiscountsToDiscountCurrent, discountsList, true);
							// }

						}
					}
				}

			} else {
				// no discount is yet applied on any sub-category, replicate
				// the printer at sub category level
				if (categoryToDiscountsListCurrent != null) {

					for (Category subCategory : subCategoriesList) {

						new EntityRelationshipManager().manageRelations(em, subCategory,
								subCategory.getCategoryToDiscounts(), discountsList, CategoryToDiscount.class);

						if (shouldManageSubcategories) {
							manageRelationForSubCategoryForDiscounts(em, subCategory, categoryRelationsHelper,
									subCategory.getCategoryToDiscounts(), discountsList, false);
						}

					}

				}

			}
		}
	}

	boolean hasIsRealTimeUpdateneededOverridden(Category parent, Category child) {
		if (parent != null && child != null) {
			if (parent.getIsRealTimeUpdateNeeded() == child.getIsRealTimeUpdateNeeded()) {
				return false;
			} else {
				return true;
			}

		}
		return false;
	}

	boolean hasCategoryToPrinterBeenOverridden(List<CategoryToPrinter> categoryToPrintersListPrev,
			List<CategoryToPrinter> categoryToPrintersOfSubCategory) {
		if (categoryToPrintersListPrev != null && categoryToPrintersOfSubCategory != null) {
			if (categoryToPrintersListPrev.size() == categoryToPrintersOfSubCategory.size()) {
				for (CategoryToPrinter categoryToPrinter : categoryToPrintersListPrev) {
					if (categoryToPrintersOfSubCategory.contains(categoryToPrinter) == false) {
						// list doesnt have this item, it cannot be equal
						return true;
					}
				}
				return false;
			} else {
				return true;
			}
		} else if (categoryToPrintersListPrev == null && categoryToPrintersOfSubCategory == null) {
			return false;
		} else {
			return true;
		}
	}

	boolean hasCategoryToDiscountsBeenOverridden(List<CategoryToDiscount> categoryToDiscountsListPrev,
			List<CategoryToDiscount> categoryToDiscountsOfSubCategory) {
		if (categoryToDiscountsListPrev != null && categoryToDiscountsOfSubCategory != null) {
			if (categoryToDiscountsListPrev.size() == categoryToDiscountsOfSubCategory.size()) {
				for (CategoryToDiscount categoryToPrinter : categoryToDiscountsListPrev) {
					if (categoryToDiscountsOfSubCategory.contains(categoryToPrinter) == false) {
						// list doesn't have this item, it cannot be equal
						return true;
					}
				}
				return false;
			} else {
				return true;
			}
		} else if (categoryToDiscountsListPrev == null && categoryToDiscountsOfSubCategory == null) {
			return false;
		} else {
			return true;
		}
	}

	void getCategoryToDiscountForCategoryFromList(List<CategoryToDiscount> categoryToDiscountsList,
			List<Category> categories) {

		for (CategoryToDiscount categoryToDiscounts : categoryToDiscountsList) {

			// get the index of category for which this category to printer
			// belongs and put in that list
			//int index = categories.indexOf(new Category(categoryToDiscounts.getCategoryId()));
			//Category category = categories.get(index);
			Category category = getCategory(categories, categoryToDiscounts);
			List<CategoryToDiscount> ctdList = category.getCategoryToDiscounts();
			if (ctdList == null) {
				ctdList = new ArrayList<CategoryToDiscount>();
				category.setCategoryToDiscounts(ctdList);
			}
			ctdList.add(categoryToDiscounts);
		}
	}
	private Category getCategory(List<Category> categories,CategoryToDiscount categoryToDiscounts) {
		for (Category category : categories) {
			if (categoryToDiscounts.getCategoryId().equals(category.getId())) {
				return category;
			}
		}
		return null;
	}

	void getCategoryToPrinterForCategoryFromList(List<CategoryToPrinter> categoryToPrinterList,
			List<Category> categories) {

		for (CategoryToPrinter categoryToPrinter : categoryToPrinterList) {

			// get the index of category for which this category to printer
			// belongs and put in that list

			if (categories != null && categories.size() > 0) {
				Category category = getCategory(categories, categoryToPrinter);
				if (category != null) {
					List<CategoryToPrinter> categoryToPrintersList = category.getCategoryToPrinters();
					if (categoryToPrintersList == null) {
						categoryToPrintersList = new ArrayList<CategoryToPrinter>();
						category.setCategoryToPrinters(categoryToPrintersList);
					}
					categoryToPrintersList.add(categoryToPrinter);
				}
			}

		}
	}

	private Category getCategory(List<Category> categories, CategoryToPrinter categoryToPrinter) {
		for (Category category : categories) {
			if (categoryToPrinter.getCategoryId().equals(category.getId())) {
				return category;
			}
		}
		return null;
	}

	Category update(EntityManager em, CatalogPacket catalogPacket) throws Exception {

		addUpdateCategory(em, catalogPacket);

		return catalogPacket.getCategory();

	}

	Category delete(EntityManager em, Category category) throws Exception {

		Category c = (Category) new CommonMethods().getObjectById("Category", em, Category.class, category.getId());
		c.setStatus("D");
		c.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(c);
		em.createNativeQuery("call p_delete_category_and_relationship( ?,?)").setParameter(1, category.getId())
				.setParameter(2, category.getUpdatedBy()).executeUpdate();

		// TODO: Ankur - what happens if the update call returns 0?
		// procedure will handle delete records :- above call is for deleting
		// relation

		return c;

	}

	List<Category> getRootCategories(EntityManager em) throws Exception {
		// fetching all root categories
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em.createQuery(criteria.select(r).where(
				builder.equal(r.get(Category_.categoryId), "0"), builder.notEqual(r.get(Category_.status), "D")));
		return query.getResultList();

	}

	List<Category> getSubCategories(EntityManager em, String categoryId) throws Exception {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.categoryId), categoryId),
						builder.notEqual(r.get(Category_.status), "D")));
		return query.getResultList();

	}

	List<Item> getAllItemsForCategory(EntityManager em, String categoryId) throws Exception {

		TypedQuery<Item> query = em
				.createQuery("select i from Item i, CategoryItem ci where ci.categoryId=? and ci.item=i", Item.class);
		query.setParameter(1, categoryId);
		return query.getResultList();

	}

	List<Category> getRootCategoriesByLocationId(EntityManager em, String locationId) throws Exception {
		LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);

		String queryString = "select c from Category c where c.locationsId=? and c.status !='D' and (c.categoryId='0' or c.categoryId is null  )";
		if (locationSetting != null && locationSetting.getItemSortingFormat() != null
				&& locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending")) {
			queryString += " order by c.displayName  asc";
		} else if (locationSetting != null && locationSetting.getItemSortingFormat() != null
				&& locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending")) {
			queryString += " order by c.displayName  desc ";
		} else {
			queryString += " order by c.sortSequence  asc";
		}

		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId);
		return query.getResultList();

	}

	List<Category> getCategoriesByCategoryId(EntityManager em, String categoryId) throws Exception {
		String queryString = "select ci from Category ci where  ci.status !='D' and ci.categoryId in (SELECT c.id FROM Category c where c.categoryId = ? or c.id = ? and c.status !='D' )  order by ci.sortSequence  asc ";
		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, categoryId)
				.setParameter(2, categoryId);
		return query.getResultList();

	}

	List<Category> getCategoriesByCategoryIdAndLocationId(EntityManager em, String categoryId, String locationId)
			throws Exception {
		String queryString = "select ci from Category ci where ci.locationsId = ? and ci.status !='D' and ci.categoryId in "
				+ "(SELECT c.id FROM Category c where c.locationsId = ? and c.categoryId = ? or c.id = ? and c.status !='D' )"
				+ "  order by ci.sortSequence  asc ";
		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId)
				.setParameter(2, locationId).setParameter(3, categoryId).setParameter(4, categoryId);
		return query.getResultList();

	}

	List<Category> getRootCategoriesByLocationIdForCustomer(EntityManager em, String locationId) throws Exception {
		String queryString = "select c from Category c where c.locationsId=? and c.status !='D' and (c.categoryId='0' or c.categoryId is null) and c.isOnlineCategory=1 order by c.sortSequence  asc";
		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId);
		return query.getResultList();

	}

	Category getRootCategoriesByLocationIdAndName(EntityManager em, String locationId, String name) {

//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
//		Root<Category> r = criteria.from(Category.class);
//		
//		 
//		TypedQuery<Category> query = em
//				.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.locationsId), locationId),
//						builder.equal(r.get(Category_.categoryId), null),
//						builder.equal(r.get(Category_.name), name), builder.notEqual(r.get(Category_.status), "D")));
//		
		String queryString = "select c from Category c where c.locationsId=? and c.name=? and c.status !='D' and (c.categoryId='0' or c.categoryId is null  )";

		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId).setParameter(2, name);
		
		return query.getSingleResult();

	}

	List<Category> getCategoriesByLocationId(EntityManager em, String locationId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.locationsId), locationId),
						builder.notEqual(r.get(Category_.status), "D")));
		return query.getResultList();

	}

	Category getRootCategoriesByLocationIdAndDisplaySequence(EntityManager em, String locationId, int displaySequence)
			throws Exception {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em.createQuery(criteria.select(r).where(
				builder.equal(r.get(Category_.locationsId), locationId), builder.equal(r.get(Category_.categoryId), 0),
				builder.equal(r.get(Category_.sortSequence), displaySequence),
				builder.notEqual(r.get(Category_.status), "D")));
		return query.getSingleResult();

	}

	Category getCategoriesById(EntityManager em, String id) throws Exception {

		CategoryRelationsHelper categoryRelationsHelper = new CategoryRelationsHelper();
		categoryRelationsHelper.setShouldEliminateDStatus(true);

		 
		Category category = (Category)new CommonMethods().getObjectById("Category", em, Category.class, id);
		category.setCategoryToPrinters(categoryRelationsHelper.getCategoryToPrinter(id, em));
		category.setCategoryToDiscounts(categoryRelationsHelper.getCategoryToDiscounts(id, em));

		return category;

	}

	Category getCategoriesByIdAndLocationId(EntityManager em, String id, String locationId) throws Exception {

		CategoryRelationsHelper categoryRelationsHelper = new CategoryRelationsHelper();
		categoryRelationsHelper.setShouldEliminateDStatus(true);

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.id), id),
				builder.equal(r.get(Category_.locationsId), locationId)));

		Category category = query.getSingleResult();

		category.setCategoryToPrinters(categoryRelationsHelper.getCategoryToPrinter(id, em));
		category.setCategoryToDiscounts(categoryRelationsHelper.getCategoryToDiscounts(id, em));

		return category;

	}

	List<Category> getCategoriesByLocationIdAndCategoryId(EntityManager em, String locationId, String categoryId)
			throws Exception {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.locationsId), locationId),
						builder.equal(r.get(Category_.categoryId), categoryId),
						builder.notEqual(r.get(Category_.status), "D")));
		return query.getResultList();

	}

	Category getCategoryByLocationIdAndRootCategoryIdAndName(EntityManager em, String locationId, String categoryId,
			String name) throws Exception {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.locationsId), locationId),
						builder.equal(r.get(Category_.categoryId), categoryId),
						builder.equal(r.get(Category_.name), name), builder.notEqual(r.get(Category_.status), "D")));
		return query.getSingleResult();

	}

	Category getCategoryByLocationIdAndRootCategoryIdAndDisplaySequence(EntityManager em, String locationId,
			String categoryId, String displaySequence) throws Exception {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.locationsId), locationId),
						builder.equal(r.get(Category_.categoryId), categoryId),
						builder.equal(r.get(Category_.sortSequence), displaySequence),
						builder.notEqual(r.get(Category_.status), "D")));
		return query.getSingleResult();

	}

	List<CatalogDisplayService> getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(EntityManager em,
			String categoryId, String locationId) throws Exception {

		List<CatalogDisplayService> ans = new ArrayList<CatalogDisplayService>();

		@SuppressWarnings("unchecked")

		List<Object[]> resultList = null;
		String queryString = "";
		LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
		if (locationSetting != null && locationSetting.getItemSortingFormat() != null
				&& locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending")) {
			queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Ascending')";
		} else if (locationSetting != null && locationSetting.getItemSortingFormat() != null
				&& locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending")) {
			queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Descending')";
		} else {
			queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'DisplaySequence')";
		}

		resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).setParameter(2, locationId)
				.getResultList();

		for (Object[] objRow : resultList) {

			// if this has primary key not 0
			if ((String) objRow[0] != null) {
				int i = 0;
				CatalogDisplayService catalogDisplayService = new CatalogDisplayService();
				if (objRow[i] != null) {
					catalogDisplayService.setId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategorylist((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemsId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemsName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setImageName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setShortName((String) objRow[i]);
				}
				i++;

				/*
				 * =============================Add by
				 * Uzma=============================
				 */
				if (objRow[i] != null) {
					catalogDisplayService.setCourseName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPriceSelling(((BigDecimal) objRow[i]).floatValue());
				}

				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPrintersName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPrintersId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setDiscountsId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategoryPrintersId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategoryDiscountsId((String) objRow[i]);
				}
				i++;
				/* ========================================================== */

				if (objRow[i] != null) {
					catalogDisplayService.setCategoryPrintersName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategoryDiscountsName((String) objRow[i]);
				}

				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemType((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setStockUom((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setSellableUom((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setDescription((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharHexCode((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharImageUrl((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setRealTimeUpdate((Integer) objRow[i]);
				}

				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setAttributeTypeCount((Integer) objRow[i]);
				}
				i++;
				logger.severe(
						"catalogDisplayService.getItemsId()========================================================="
								+ catalogDisplayService.getItemsId());

				if (catalogDisplayService.getItemsId() != null && catalogDisplayService.getItemsId() != null) {
					Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
							catalogDisplayService.getItemsId());
			
					catalogDisplayService.setAvailability(item.isAvailability());
					catalogDisplayService.setStatus(item.getStatus());

				}
				ans.add(catalogDisplayService);
			}

		}

		return ans;

	}

	List<CatalogDisplayService> getCategoryAndSubCategoryAndItemByLocationId(EntityManager em, String locationId)
			throws Exception {

		List<CatalogDisplayService> ans = new ArrayList<CatalogDisplayService>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getcategorylist_new(?)").setParameter(1, locationId)
				.getResultList();

		for (Object[] objRow : resultList) {

			// if this has primary key not 0
			if ((String) objRow[0] != null) {
				int i = 0;
				CatalogDisplayService catalogDisplayService = new CatalogDisplayService();
				if (objRow[i] != null) {
					catalogDisplayService.setId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategorylist((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemsId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemsName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setImageName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setShortName((String) objRow[i]);
				}
				i++;

				if (objRow[i] != null) {
					catalogDisplayService.setCourseName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPriceSelling(((BigDecimal) objRow[i]).floatValue());
				}
				i++;
				ans.add(catalogDisplayService);
			}

		}

		return ans;

	}

	List<CatalogDisplayService> getAllCategoriesAndItemByCategoryId(EntityManager em, String categoryId,
			String locationId) throws Exception {

		List<CatalogDisplayService> ans = new ArrayList<CatalogDisplayService>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_categoryList(?,?)").setParameter(1, categoryId)
				.setParameter(2, locationId).getResultList();

		for (Object[] objRow : resultList) {

			if ((String) objRow[0] != null) {
				int i = 0;
				CatalogDisplayService catalogDisplayService = new CatalogDisplayService();
				if (objRow[i] != null) {
					catalogDisplayService.setId((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setCategorylist((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setItemsId((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setItemsName((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setImageName((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setShortName((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setCourseName((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setPriceSelling((float) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setPrintersName((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setPrintersId((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setDiscountsId((String) objRow[i++]);
				}
				if (objRow[i] != null) {

					catalogDisplayService.setCategoryPrintersId((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setCategoryDiscountsId((String) objRow[i++]);
				}
				if (objRow[i] != null) {

					catalogDisplayService.setCategoryPrintersName((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setCategoryDiscountsName((String) objRow[i++]);
				}
				if (objRow[i] != null) {

					catalogDisplayService.setItemType((int) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setStockUom((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setSellableUom((String) objRow[i++]);
				}

				if (objRow[i] != null) {
					catalogDisplayService.setDiscription((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharId((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharHexCode((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharImageUrl((String) objRow[i++]);
				}
				if (objRow[i] != null) {

					catalogDisplayService.setRealTimeUpdate((Integer) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setAttributeTypeCount((Integer) objRow[i++]);
				}

				ans.add(catalogDisplayService);
			}

		}

		return ans;
	}

	List<CatalogDisplayService> getAllCategoriesAndItemForCustomerByCategoryIdAndLocationId(EntityManager em,
			String categoryId, String locationId) throws Exception {
		List<CatalogDisplayService> ans = new ArrayList<CatalogDisplayService>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getcategoryanditemslist(?,?)")
				.setParameter(1, categoryId).setParameter(2, locationId).getResultList();

		for (Object[] objRow : resultList) {

			// if this has primary key not 0
			if ((String) objRow[0] != null) {
				int i = 0;
				CatalogDisplayService catalogDisplayService = new CatalogDisplayService();
				if (objRow[i] != null) {
					catalogDisplayService.setId((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setCategorylist((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setItemsId((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setItemsName((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setImageName((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setDiscription((String) objRow[i++]);
				}
				if (objRow[i] != null) {
					catalogDisplayService.setCourseName((String) objRow[i++]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPriceSelling((float) objRow[i++]);
				}

				ans.add(catalogDisplayService);
			}

		}

		return ans;

	}

	List<Category> getAllCategoriesByLocationId(EntityManager em, String locationId) {

		String queryString = "select c from Category c where c.locationsId= '" + locationId
				+ "' and  c.status not in ('D','I')  "
				+ "and (c.categoryId  NOT IN "
				+ "(select id from Category  where locationsId=? and status in ('D','I')  ) or c.categoryId is null )";
		
		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId);
		return query.getResultList();

	}

	List<Category> getBusinessTypeForServicPlanByCountryId(EntityManager em, int countryId) throws Exception {

		String queryString = "SELECT c.id, c.name, c.category_id, c.display_name, c.description, "
				+ " c.locations_id, c.icon_colour, c.image_name" + " FROM category c"
				+ " JOIN `locations` l ON c.locations_id = l.id" + " JOIN address a ON l.address_id = a.id"
				+ " WHERE country_id = ? " + " AND (l.locations_id ='0' or l.locations_id is null) and (c.category_id='0' or c.category_id is null ) "
				+ " AND l.status =  'A' AND c.status='A' ";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, countryId).getResultList();

		List<Category> categoryList = new ArrayList<Category>();
		for (Object[] objRow : resultList) {

			if ((String) objRow[0] != null) {
				int i = 0;
				Category category = new Category();
				category.setId((String) objRow[i++]);
				category.setName((String) objRow[i++]);
				category.setCategoryId((String) objRow[i++]);
				category.setDisplayName((String) objRow[i++]);
				category.setDescription((String) objRow[i++]);
				category.setLocationsId((String) objRow[i++]);
				category.setIconColour((String) objRow[i++]);
				category.setImageName((String) objRow[i++]);

				categoryList.add(category);

			}

		}
		return categoryList;

	}

	List<Item> getAllItemByCategoryId(EntityManager em, String categoryId) throws Exception {

		String queryString = "select i.id,i.item_number,  i.name, i.display_name, i.short_name,i.description, i.price_selling from items i left join category_items ci on ci.items_id = i.id "
				+ " left join category c on c.id = ci.category_id JOIN  `locations` l ON i.locations_id = l.id WHERE (l.locations_id ='0' or l.locations_id is null) and c.id=? "
				+ " AND l.status =  'A' AND c.status='A' and i.status='A' order by i.display_sequence ";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).getResultList();

		List<Item> itemList = new ArrayList<Item>();
		for (Object[] objRow : resultList) {
			Item item = new Item();
			int i = 0;
			String itemId = (String) objRow[i++];

			List<ItemsAttribute> itemsAttribute = getItemsAttributeByItemId(itemId, em);

			item.setId(itemId);
			item.setItemNumber((String) objRow[i++]);
			item.setName((String) objRow[i++]);
			item.setDisplayName((String) objRow[i++]);
			item.setShortName((String) objRow[i++]);
			item.setDescription((String) objRow[i++]);
			item.setPriceSelling((BigDecimal) objRow[i++]);
			item.setItemsAttributes(itemsAttribute);
			itemList.add(item);
		}
		return itemList;

	}

	private List<ItemsAttribute> getItemsAttributeByItemId(String itemId, EntityManager em) {
		List<ItemsAttribute> itemAttrList = new ArrayList<ItemsAttribute>();
		String queryString = "SELECT ia.id, ia.display_name ,iatia.items_attribute_type_id,ia.multi_select,iat.is_required, ia.selling_price  FROM  items AS i "
				+ " LEFT JOIN items_to_items_attribute AS ita ON ita.items_id = i.id and ita.status='A' LEFT JOIN items_attribute AS ia  ON ita.items_attribute_id = ia.id and ia.status='A' "
				+ " LEFT JOIN items_attribute_type_to_items_attribute AS iatia  ON iatia.items_attribute_id = ia.id "
				+ " LEFT JOIN items_attribute_type AS iat  ON iat.id= iatia.items_attribute_type_id and ia.status='A'  where i.id=?";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, itemId).getResultList();
		for (Object[] objRow : resultList) {
			// if this has primary key not 0
			ItemsAttribute itemAttr = new ItemsAttribute();
			if (objRow[0] != null) {
				itemAttr.setId((String) objRow[0]);
				itemAttr.setDisplayName((String) objRow[1]);
				itemAttr.setItemsAttributeTypeId((String) objRow[2]);
				itemAttr.setMultiSelect((byte) objRow[3]);
				Integer i = (boolean) (objRow[4]) ? 1 : 0;
				itemAttr.setIsRequired(i);
				itemAttr.setSellingPrice((BigDecimal) objRow[5]);
			}
			itemAttrList.add(itemAttr);
		}

		return itemAttrList;
	}

	List<Category> getSubCategoryByCategoryId(EntityManager em, String categoryId) throws Exception {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em.createQuery(criteria.select(r).where(
				builder.equal(r.get(Category_.categoryId), categoryId), builder.equal(r.get(Category_.status), "A")));
		return query.getResultList();

	}

	List<CatalogDisplayService> getCategoryItemByCategoryIdAndLocationId(EntityManager em, String categoryId,
			String locationId) throws Exception {

		List<CatalogDisplayService> ans = new ArrayList<CatalogDisplayService>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_getCategoryItemByCategoryIdAndLocationId(?,?)")
				.setParameter(1, categoryId).setParameter(2, locationId).getResultList();

		for (Object[] objRow : resultList) {

			// if this has primary key not 0
			if ((String) objRow[0] != null) {
				int i = 0;
				CatalogDisplayService catalogDisplayService = new CatalogDisplayService();
				if (objRow[i] != null) {
					catalogDisplayService.setId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategorylist((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemsId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemsName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setImageName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setShortName((String) objRow[i]);
				}
				i++;

				/*
				 * =============================Add by
				 * Uzma=============================
				 */
				if (objRow[i] != null) {
					catalogDisplayService.setCourseName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPriceSelling(((BigDecimal) objRow[i]).floatValue());
				}
				i++;

				if (objRow[i] != null) {
					catalogDisplayService.setStartTime(((String) objRow[i]));
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setEndTime(((String) objRow[i]));
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPrintersName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPrintersId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setDiscountsId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategoryPrintersId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategoryDiscountsId((String) objRow[i]);
				}
				i++;
				/* ========================================================== */

				if (objRow[i] != null) {
					catalogDisplayService.setCategoryPrintersName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategoryDiscountsName((String) objRow[i]);
				}

				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemType((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setStockUom((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setSellableUom((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setDescription((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharHexCode((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharImageUrl((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setRealTimeUpdate((Integer) objRow[i]);
				}

				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setAttributeTypeCount((Integer) objRow[i]);
				}
				i++;

				if (objRow[i] != null) {
					catalogDisplayService.setCourseId((String) objRow[i]);
				}
				i++;

				ans.add(catalogDisplayService);
			}

		}

		return ans;

	}

	List<Category> getCategoriesByCategoryIdPacket(EntityManager em, CategoryIdPacket categoryIdPacket)
			throws Exception {
		String CategoryIds = Arrays.toString(categoryIdPacket.getCategoryIds()).replace("[", "").replace("]", "");

		String queryString = "select ci from Category ci where  ci.status !='D' and ci.categoryId in (SELECT c.id FROM Category c where c.categoryId in ("
				+ CategoryIds + ")  or c.id in (" + CategoryIds
				+ ") and c.status !='D' )  order by ci.sortSequence  asc ";
		TypedQuery<Category> query = em.createQuery(queryString, Category.class);
		return query.getResultList();

	}

	List<Item> getCategoryAndSubCategoryAndItemByCategoryIdPacketAndLocationId(EntityManager em,
			CategoryIdPacket categoryIdPacket, String locationId) throws Exception {
		String arr[] = categoryIdPacket.getCategoryIds();
		String categoryIds = "";
		for(int i =0;i<arr.length;i++){
			if(i==(categoryIdPacket.getCategoryIds().length-1)){
				categoryIds += "'"+arr[i]+"'";
			}else{
				
				categoryIds += "'"+arr[i]+"'" +",";
			}
			logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@------"+categoryIds);
		}
		String queryString = "select i.id,i.item_number,i.name,i.display_name,i.short_name,i.description,i.price_selling,i.availability  "
				+ "from items i join category_items ci on ci.items_id = i.id"
				+ " left join category c on c.id = ci.category_id" + " where c.id in (" + categoryIds
				+ ") and i.status='A'  order by i.display_sequence  asc ";
		logger.severe("sssssssssssssqqqqqqqqllll@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@------"+queryString);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).getResultList();

		List<Item> itemList = new ArrayList<Item>();
		for (Object[] objRow : resultList) {
			Item item = new Item();
			int i = 0;
			String itemId = (String) objRow[i++];

			item.setId(itemId);
			item.setItemNumber((String) objRow[i++]);
			item.setName((String) objRow[i++]);
			item.setDisplayName((String) objRow[i++]);
			item.setShortName((String) objRow[i++]);
			item.setDescription((String) objRow[i++]);
			item.setPriceSelling((BigDecimal) objRow[i++]);
			boolean availability = BooleanUtils.toBoolean((Integer) objRow[i++]);
			item.setAvailability((boolean) availability);
			try {
				item.setCategoryItems(new ItemRelationsHelper().getCategoryItem(itemId, em));
			} catch (Exception e) {
				logger.severe(e);
			}
			itemList.add(item);
		}
		return itemList;

	}

	List<CatalogDisplayService> getcategoryanditemslist(EntityManager em, String categoryId, String locationId)
			throws Exception {

		List<CatalogDisplayService> ans = new ArrayList<CatalogDisplayService>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getcategoryanditemslist(?,?)")
				.setParameter(1, categoryId).setParameter(2, locationId).getResultList();

		for (Object[] objRow : resultList) {

			// if this has primary key not 0
			if ((String) objRow[0] != null) {
				int i = 0;
				CatalogDisplayService catalogDisplayService = new CatalogDisplayService();
				if (objRow[i] != null) {
					catalogDisplayService.setId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCategorylist((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemsId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemsName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setImageName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setShortName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setCourseName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setPriceSelling(((BigDecimal) objRow[i]).floatValue());
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharHexCode((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					catalogDisplayService.setItemCharImageUrl((String) objRow[i]);
				}

				ans.add(catalogDisplayService);
			}

		}

		return ans;

	}

	Category addMultipleLocationsCategory(EntityManager em, Category category, CatalogPacket categoryPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = categoryPacket.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		CatalogPacket myPacket = new CatalogPacket();
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (category != null && categoryPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			// category.setLocationsId(baseLocation.getId());

			Category globalCategory = addUpdateCategory(em, categoryPacket);
			categoryPacket.setCategory(globalCategory);
			
			categoryPacket.setLocationsListId("");
			
			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			for (String locationsId : locationIds) {
				
				
				if (locationsId.length() > 0 && !locationsId.equals(baseLocation.getId())) {
					categoryPacket.setLocalServerURL(0);
					String json = new StoreForwardUtility().returnJsonPacket(categoryPacket, "CatalogPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
							Integer.parseInt(categoryPacket.getMerchantId()));
					Category localcategory = new Category().getCategory(globalCategory);
					localcategory.setLocationsId(locationsId);
					if (localcategory.getCategoryId() != null) {
						String parentCategoryId = getLocalCategoryIdFromGlobalId(em, localcategory.getCategoryId(),
								locationsId);
						localcategory.setCategoryId(parentCategoryId);
					}
					List<Printer> printerList = getLocalItemToPrinters(em, locationsId,
							categoryPacket.getPrinterList());
					myPacket.setPrinterList(printerList);

					List<Discount> discountList = getLocalItemToDiscounts(em, locationsId,
							categoryPacket.getDiscountsList());

					localcategory.setGlobalCategoryId(globalCategory.getId());

					if (categoryPacket.getCategory().getItemGroupId() != null) {
						localcategory.setItemGroupId(
								getLocalItemGroup(em, locationsId, categoryPacket.getCategory().getItemGroupId()));
					}

					myPacket.setDiscountsList(discountList);
					myPacket.setCategory(localcategory);

					Category c = addUpdateCategory(em, myPacket);
					c.setLocationsId(locationsId);
					categoryPacket.setCategory(c);
					categoryPacket.setLocalServerURL(0);
					categoryPacket.setLocationsListId("");
					String json2 = new StoreForwardUtility().returnJsonPacket(categoryPacket, "CatalogPacket", request);
					logger.severe(
							"json2================================================================" + json2);

					
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, locationsId,
							Integer.parseInt(categoryPacket.getMerchantId()));
				}
			}
		}
		return category;
	}

	Category updateMultipleLocationsCategory(EntityManager em, Category category, CatalogPacket catalogPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (catalogPacket.getLocationId().trim().length() > 0) {
			locationIds = catalogPacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		CatalogPacket myPacket = createNewCatalogPacket(catalogPacket);

		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (category != null && catalogPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item

			Category globalCategory = addUpdateCategory(em, catalogPacket);
			catalogPacket.setCategory(globalCategory);
			catalogPacket.setLocationsListId("");
			
			// now add/update child location
			if (locationIds != null) {
				for (String locationsId : locationIds) {
					
					if (locationsId.length() > 0 && !locationsId.equals(baseLocation.getId())) {
						String json = new StoreForwardUtility().returnJsonPacket(catalogPacket, "CatalogPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(catalogPacket.getMerchantId()));
						Category localCategory = new Category().getCategory(category);

						Category localCategoryFromDB = getCategoryByGlobalIdAndLocationId(em, globalCategory.getId(),
								locationsId);
						if (localCategoryFromDB != null) {
							localCategory.setId(localCategoryFromDB.getId());
							localCategory.setSortSequence(localCategoryFromDB.getSortSequence());
						}

						Category categories = getCategoryByGlobalPrinterIdAndLocationId(em, locationsId,
								globalCategory.getId());
						if (categories != null && categories.getId() != null) {
							localCategory.setGlobalCategoryId(categories.getGlobalCategoryId());
							localCategory.setId(categories.getId());
						} else {
							localCategory.setGlobalCategoryId(globalCategory.getId());
						}
						if (localCategory.getCategoryId() != null) {
							String parentCategoryId = getLocalCategoryIdFromGlobalId(em, localCategory.getCategoryId(),
									locationsId);
							localCategory.setCategoryId(parentCategoryId);
						}

						List<Printer> printerList = getLocalItemToPrinters(em, locationsId,
								catalogPacket.getPrinterList());
						myPacket.setPrinterList(printerList);

						List<Discount> discountList = getLocalItemToDiscounts(em, locationsId,
								catalogPacket.getDiscountsList());
						myPacket.setDiscountsList(discountList);

						localCategory.setLocationsId((locationsId));

						if (catalogPacket.getCategory().getItemGroupId() != null) {
							localCategory.setItemGroupId(
									getLocalItemGroup(em, locationsId, catalogPacket.getCategory().getItemGroupId()));
						}

						myPacket.setCategory(localCategory);
						Category c = addUpdateCategory(em, myPacket);
						catalogPacket.setCategory(c);
						catalogPacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(catalogPacket, "CatalogPacket",
								request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request, locationsId,
								Integer.parseInt(catalogPacket.getMerchantId()));
					}
				}
			}
		}
		return category;
	}

	Category deleteMultipleLocationCategory(EntityManager em, Category category, HttpServletRequest request)
			throws Exception {
		// delete baselocation
		category = delete(em, category);
		// get all sublocations
		List<Category> categories = getCategoryByGlobalId(em, category.getId());
		// delete sublocation
		for (Category category2 : categories) {
			delete(em, category2);
		}
		return category;
	}

	private Category getCategoryByGlobalPrinterIdAndLocationId(EntityManager em, String locationId,
			String globalCategoryId) throws Exception {
		Category globalCategory = getCategoriesById(em, globalCategoryId);
		if (globalCategory.getCategoryId() != null) {
			Category localCategory = null;
			try {
				String queryString = "select s from Category s where s.globalCategoryId =? and s.locationsId=? and s.status !='D' ";
				TypedQuery<Category> query = em.createQuery(queryString, Category.class)
						.setParameter(1, globalCategory.getCategoryId()).setParameter(2, locationId);
				localCategory = query.getSingleResult();
				if (localCategory != null) {
					try {
						String queryString1 = "select s from Category s where s.globalCategoryId =? and s.locationsId=? and s.status !='D' and s.categoryId=? ";
						TypedQuery<Category> query1 = em.createQuery(queryString1, Category.class)
								.setParameter(1, globalCategoryId).setParameter(2, locationId)
								.setParameter(3, localCategory.getId());
						return query1.getSingleResult();
					} catch (NoResultException e) {
						logger.severe(e);

					}
				}
			} catch (NoResultException e) {
				logger.severe(e);

			}
		} else {

			try {
				String queryString = "select s from Category s where s.globalCategoryId =? and s.locationsId=? and s.status !='D' ";
				TypedQuery<Category> query = em.createQuery(queryString, Category.class)
						.setParameter(1, globalCategoryId).setParameter(2, locationId);
				return query.getSingleResult();
			} catch (NoResultException e) {
				logger.severe(e);

			}
		}

		return null;

	}

	private List<Category> getCategoryByGlobalId(EntityManager em, String globalCategoryId) {
		try {
			// no need to check status as this method for deletion
			String queryString = "select s from Category s where s.globalCategoryId =? ";
			TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, globalCategoryId);
			return query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);

		}
		return null;
	}

	private Category getCategoryByGlobalIdAndLocationId(EntityManager em, String globalCategoryId, String locationId) {
		try {
			String queryString = "select s from Category s where s.globalCategoryId =? and s.locationsId =? and s.status!='D' ";
			TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, globalCategoryId)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.severe(e);

		}
		return null;
	}

	public String getCategoryByLocationId(String locationId, int startIndex, int endIndex, String categoryName,
			EntityManager em, HttpServletRequest httpRequest) throws Exception {
		try {
			List<CategoryDetailDisplayPacket> ans = new ArrayList<CategoryDetailDisplayPacket>();
			String sql = " select c.id as c_id,c.display_name as c_display_name,c.description as c_description "
					+ " from category c where c.locations_id=?";

			sql += " and c.display_name like '%" + categoryName + "%' and c.status !='D' and c.status !='R' limit "
					+ startIndex + "," + endIndex;

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();

			for (Object[] objRow : resultList) {
				CategoryDetailDisplayPacket detailDisplayPacket = new CategoryDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setCategoryDisplayName((String) objRow[1]);
				detailDisplayPacket.setDescription((String) objRow[2]);
				ans.add(detailDisplayPacket);
			}

			for (CategoryDetailDisplayPacket categoryDetailDisplayPacket : ans) {
				List<CategoryToPrinter> categoryToPrinters = getCategoryToPrinter(em,
						categoryDetailDisplayPacket.getId());
				List<CategoryToDiscount> categoryToDiscounts = getCategoryToDiscounts(em,
						categoryDetailDisplayPacket.getId());
				String categoryToPrinter = "";
				String categoryToDiscount = "";
				String categoryToPrinterName = "";
				String categoryToDiscountName = "";

				for (int i = 0; i < categoryToPrinters.size(); i++) {
					CategoryToPrinter categoryToPrinter2 = categoryToPrinters.get(i);
					Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em, Printer.class,
							categoryToPrinter2.getPrintersId());
					if (printer != null) {
						if (i == (categoryToPrinters.size() - 1)) {
							categoryToPrinterName += printer.getDisplayName();
							categoryToPrinter += categoryToPrinter2.getId();
						} else {
							categoryToPrinterName += printer.getDisplayName() + ",";
							categoryToPrinter += categoryToPrinter2.getId() + ",";
						}
					}
				}
				for (int i = 0; i < categoryToDiscounts.size(); i++) {
					CategoryToDiscount categoryToDiscount2 = categoryToDiscounts.get(i);
					Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
							categoryToDiscount2.getDiscountsId());
					if (discount != null) {
						if (i == (categoryToDiscounts.size() - 1)) {
							categoryToDiscountName += discount.getDisplayName();
							categoryToDiscount += categoryToDiscount2.getId();
						} else {
							categoryToDiscountName += discount.getDisplayName() + ",";
							categoryToDiscount += categoryToDiscount2.getId() + ",";
						}
					}
				}
				categoryDetailDisplayPacket.setCategoryToPrinters(categoryToPrinter);
				categoryDetailDisplayPacket.setCategoryToDiscounts(categoryToDiscount);
				categoryDetailDisplayPacket.setCategoryToPrintersName(categoryToPrinterName);
				categoryDetailDisplayPacket.setCategoryToDiscountsName(categoryToDiscountName);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public BigInteger getCategoryCountByLocationId(String locationId, String categoryName, EntityManager em,
			HttpServletRequest httpRequest) throws Exception {
		try {
			List<CategoryDetailDisplayPacket> ans = new ArrayList<CategoryDetailDisplayPacket>();
			String sql = " select c.id as c_id,c.display_name as c_display_name,c.description as c_description "
					+ " from category c where c.locations_id=?";

			sql += " and c.display_name like '%" + categoryName + "%' and c.status !='D' and c.status !='R'";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();

			for (Object[] objRow : resultList) {
				CategoryDetailDisplayPacket detailDisplayPacket = new CategoryDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setCategoryDisplayName((String) objRow[1]);
				detailDisplayPacket.setDescription((String) objRow[2]);
				ans.add(detailDisplayPacket);
			}

			for (CategoryDetailDisplayPacket categoryDetailDisplayPacket : ans) {
				List<CategoryToPrinter> categoryToPrinters = getCategoryToPrinter(em,
						categoryDetailDisplayPacket.getId());
				List<CategoryToDiscount> categoryToDiscounts = getCategoryToDiscounts(em,
						categoryDetailDisplayPacket.getId());
				String categoryToPrinter = "";
				String categoryToDiscount = "";
				String categoryToPrinterName = "";
				String categoryToDiscountName = "";

				for (int i = 0; i < categoryToPrinters.size(); i++) {
					CategoryToPrinter categoryToPrinter2 = categoryToPrinters.get(i);
					Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em, Printer.class,
							categoryToPrinter2.getPrintersId());
					if (printer != null) {
						if (i == (categoryToPrinters.size() - 1)) {
							categoryToPrinterName += printer.getDisplayName();
							categoryToPrinter += categoryToPrinter2.getId();
						} else {
							categoryToPrinterName += printer.getDisplayName() + ",";
							categoryToPrinter += categoryToPrinter2.getId() + ",";
						}
					}
				}
				for (int i = 0; i < categoryToDiscounts.size(); i++) {
					CategoryToDiscount categoryToDiscount2 = categoryToDiscounts.get(i);
					Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
							categoryToDiscount2.getDiscountsId());
					if (discount != null) {
						if (i == (categoryToDiscounts.size() - 1)) {
							categoryToDiscountName += discount.getDisplayName();
							logger.severe(
									"categoryToDiscount2.getId()================================================================"
											+ categoryToDiscount2.getId());
							categoryToDiscount += categoryToDiscount2.getId();
						} else {
							categoryToDiscountName += discount.getDisplayName() + ",";
							categoryToDiscount += categoryToDiscount2.getId() + ",";
						}
					}
				}
				categoryDetailDisplayPacket.setCategoryToPrinters(categoryToPrinter);
				categoryDetailDisplayPacket.setCategoryToDiscounts(categoryToDiscount);
				categoryDetailDisplayPacket.setCategoryToPrintersName(categoryToPrinterName);
				categoryDetailDisplayPacket.setCategoryToDiscountsName(categoryToDiscountName);
			}
			return BigInteger.valueOf(ans.size());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private List<CategoryToPrinter> getCategoryToPrinter(EntityManager em, String categoryId) {
		try {
			String queryString = "select p from CategoryToPrinter p where p.categoryId=? and p.status != 'D' ";
			TypedQuery<CategoryToPrinter> query = em.createQuery(queryString, CategoryToPrinter.class).setParameter(1,
					categoryId);
			return query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
			logger.severe("no category to printer result found for categoryid" + categoryId);
		}
		return null;
	}

	private List<CategoryToDiscount> getCategoryToDiscounts(EntityManager em, String categoryId) {
		try {
			String queryString = "select p from CategoryToDiscount p where p.categoryId=? and p.status != 'D' ";
			TypedQuery<CategoryToDiscount> query = em.createQuery(queryString, CategoryToDiscount.class).setParameter(1,
					categoryId);
			return query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
			logger.severe("no category to discount result found for categoryid" + categoryId);
		}
		return null;
	}

	private List<Printer> getLocalItemToPrinters(EntityManager em, String locationId,
			List<Printer> globalPrintersList) {
		List<Printer> localItemsToPrinters = new ArrayList<Printer>();
		if(globalPrintersList!=null){
		for (Printer globalprinter : globalPrintersList) {
			String localId = getLocalPrinterFromGlobalPrinterId(em, globalprinter.getId(), locationId);
			if (localId != null) {
				Printer local = new Printer();
				local.setId(localId);
				localItemsToPrinters.add(local);
			}
		}
		}
		return localItemsToPrinters;
	}

	private String getLocalPrinterFromGlobalPrinterId(EntityManager em, String globalPrinterId, String locationId) {
		try {
			String queryString = "select s from Printer s where s.globalPrinterId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<Printer> query = em.createQuery(queryString, Printer.class).setParameter(1, globalPrinterId)
					.setParameter(2, locationId);
			return query.getSingleResult().getId();
		} catch (NoResultException e) {
			logger.severe(e);

		}
		return null;
	}

	private String getLocalCategoryIdFromGlobalId(EntityManager em, String globalPrinterId, String locationId) {
		try {
			String queryString = "select s from Category s where s.globalCategoryId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, globalPrinterId)
					.setParameter(2, locationId);
			return query.getSingleResult().getId();
		} catch (NoResultException e) {
			logger.severe(e);

		}
		return null;
	}

	private List<Discount> getLocalItemToDiscounts(EntityManager em, String locationId, List<Discount> globalList) {
		List<Discount> localItemsToDiscounts = new ArrayList<Discount>();
		if(globalList!=null){
		for (Discount globalprinter : globalList) {
			String localId = getLocalDiscountFromGlobalId(em, globalprinter.getId(), locationId);
			if (localId != null) {
				Discount local = new Discount();
				local.setId(localId);
				localItemsToDiscounts.add(local);
			}

		}}
		return localItemsToDiscounts;
	}

	private String getLocalDiscountFromGlobalId(EntityManager em, String globalId, String locationId) {
		try {
			String queryString = "select s from Discount s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<Discount> query = em.createQuery(queryString, Discount.class).setParameter(1, globalId)
					.setParameter(2, locationId);
			return query.getSingleResult().getId();
		} catch (NoResultException e) {
			logger.severe(e);
			logger.severe("No Discount forund for globalId " + globalId + " locationsId " + locationId + " " + e);

		}
		return null;
	}

	List<Category> getRawMaterialByNameAndLocationId(EntityManager em, String locationId) throws Exception {
		String queryString = "select c from Category c where c.locationsId=? and c.status !='D' and c.name='Raw Material' order by c.sortSequence  asc";
		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId);
		return query.getResultList();

	}

	List<CategoryDetailDisplayPacket> getRawMaterialCategoryByStatusAndLocationId(EntityManager em, String locationId,
			int startIndex, int endIndex, String categoryName) throws Exception {
		try {
			String temp = Utilities.convertAllSpecialCharForSearch(categoryName);

			List<CategoryDetailDisplayPacket> ans = new ArrayList<CategoryDetailDisplayPacket>();
			String sql = " select c.id as c_id,c.display_name as c_display_name,c.description as c_description "
					+ " from category c where c.locations_id=?";

			sql += " and c.display_name like '%" + temp + "%' and c.status ='R' limit " + startIndex + "," + endIndex;

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();

			for (Object[] objRow : resultList) {
				CategoryDetailDisplayPacket detailDisplayPacket = new CategoryDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setCategoryDisplayName((String) objRow[1]);
				detailDisplayPacket.setDescription((String) objRow[2]);
				ans.add(detailDisplayPacket);
			}

			for (CategoryDetailDisplayPacket categoryDetailDisplayPacket : ans) {
				List<CategoryToPrinter> categoryToPrinters = getCategoryToPrinter(em,
						categoryDetailDisplayPacket.getId());
				List<CategoryToDiscount> categoryToDiscounts = getCategoryToDiscounts(em,
						categoryDetailDisplayPacket.getId());
				String categoryToPrinter = "";
				String categoryToDiscount = "";
				String categoryToPrinterName = "";
				String categoryToDiscountName = "";

				for (int i = 0; i < categoryToPrinters.size(); i++) {
					CategoryToPrinter categoryToPrinter2 = categoryToPrinters.get(i);
					Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em, Printer.class,
							categoryToPrinter2.getPrintersId());
					if (printer != null) {
						if (i == (categoryToPrinters.size() - 1)) {
							categoryToPrinterName += printer.getDisplayName();
							categoryToPrinter += categoryToPrinter2.getId();
						} else {
							categoryToPrinterName += printer.getDisplayName() + ",";
							categoryToPrinter += categoryToPrinter2.getId() + ",";
						}
					}
				}
				for (int i = 0; i < categoryToDiscounts.size(); i++) {
					CategoryToDiscount categoryToDiscount2 = categoryToDiscounts.get(i);
					Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
							categoryToDiscount2.getDiscountsId());
					if (discount != null) {
						if (i == (categoryToDiscounts.size() - 1)) {
							categoryToDiscountName += discount.getDisplayName();
							categoryToDiscount += categoryToDiscount2.getId();
						} else {
							categoryToDiscountName += discount.getDisplayName() + ",";
							categoryToDiscount += categoryToDiscount2.getId() + ",";
						}
					}
				}
				categoryDetailDisplayPacket.setCategoryToPrinters(categoryToPrinter);
				categoryDetailDisplayPacket.setCategoryToDiscounts(categoryToDiscount);
				categoryDetailDisplayPacket.setCategoryToPrintersName(categoryToPrinterName);
				categoryDetailDisplayPacket.setCategoryToDiscountsName(categoryToDiscountName);
			}
			return ans;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	int getRawMaterialCategoryCountByLocationId(EntityManager em, String locationId) throws Exception {
		String queryString = "select c from Category c where c.locationsId=? and c.status ='R' order by c.sortSequence  asc";
		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId);
		if (query.getResultList() != null) {
			return query.getResultList().size();
		} else {
			return 0;
		}

	}

	public String getCategoryAndSubCategoryByLocationIdCategoryName(String locationId, String categoryName,
			EntityManager em, HttpServletRequest httpRequest) throws Exception {
		String sql = "";
		// if searching for first time
		if (categoryName.length() == 0) {
			sql = " select c.id as c_id,c.display_name as c_display_name,c.description as c_description "
					+ " from category c where c.locations_id=? and (c.category_id='0' || c.category_id is null) and  c.display_name like '%"
					+ categoryName + "%' and c.status !='D'  ";
		} else {
			sql = " select c.id as c_id,c.display_name as c_display_name,c.description as c_description "
					+ " from category c where c.locations_id=? and  c.display_name like '%" + categoryName
					+ "%' and c.status !='D'  ";
		}
		List<CategoryDetailDisplayPacket> ans = new ArrayList<CategoryDetailDisplayPacket>();
		List<CategoryDisplayPacket> categoryPacket = new ArrayList<CategoryDisplayPacket>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList) {
			CategoryDetailDisplayPacket detailDisplayPacket = new CategoryDetailDisplayPacket();
			detailDisplayPacket.setId((String) objRow[0]);
			detailDisplayPacket.setCategoryDisplayName((String) objRow[1]);
			detailDisplayPacket.setDescription((String) objRow[2]);
			ans.add(detailDisplayPacket);
		}

		for (CategoryDetailDisplayPacket categoryDetailDisplayPacket : ans) {
			CategoryDisplayPacket displayPacket = new CategoryDisplayPacket();
			List<CategoryToPrinter> categoryToPrinters = getCategoryToPrinter(em, categoryDetailDisplayPacket.getId());
			List<CategoryToDiscount> categoryToDiscounts = getCategoryToDiscounts(em,
					categoryDetailDisplayPacket.getId());
			String categoryToPrinter = "";
			String categoryToDiscount = "";
			String categoryToPrinterName = "";
			String categoryToDiscountName = "";

			for (int i = 0; i < categoryToPrinters.size(); i++) {
				CategoryToPrinter categoryToPrinter2 = categoryToPrinters.get(i);
				Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em, Printer.class,
						categoryToPrinter2.getPrintersId());
				if (printer != null) {
					if (i == (categoryToPrinters.size() - 1)) {
						categoryToPrinterName += printer.getDisplayName();
						categoryToPrinter += categoryToPrinter2.getId();
					} else {
						categoryToPrinterName += printer.getDisplayName() + ",";
						categoryToPrinter += categoryToPrinter2.getId() + ",";
					}
				}
			}
			for (int i = 0; i < categoryToDiscounts.size(); i++) {
				CategoryToDiscount categoryToDiscount2 = categoryToDiscounts.get(i);
				Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
						categoryToDiscount2.getDiscountsId());
				if (discount != null) {
					if (i == (categoryToDiscounts.size() - 1)) {
						categoryToDiscountName += discount.getDisplayName();
						categoryToDiscount += categoryToDiscount2.getId();
					} else {
						categoryToDiscountName += discount.getDisplayName() + ",";
						categoryToDiscount += categoryToDiscount2.getId() + ",";
					}
				}
			}
			categoryDetailDisplayPacket.setCategoryToPrinters(categoryToPrinter);
			categoryDetailDisplayPacket.setCategoryToDiscounts(categoryToDiscount);
			categoryDetailDisplayPacket.setCategoryToPrintersName(categoryToPrinterName);
			categoryDetailDisplayPacket.setCategoryToDiscountsName(categoryToDiscountName);
			displayPacket.setCategory(categoryDetailDisplayPacket);
			displayPacket.setSubCategory(getSubCategoryByLocationIdCategoryName(locationId,
					categoryDetailDisplayPacket.getId(), em, httpRequest));
			categoryPacket.add(displayPacket);

		}

		return new JSONUtility(httpRequest).convertToJsonString(categoryPacket);
	}

	public List<CategoryDetailDisplayPacket> getSubCategoryByLocationIdCategoryName(String locationId,
			String categoryId, EntityManager em, HttpServletRequest httpRequest) throws Exception {

		List<CategoryDetailDisplayPacket> ans = new ArrayList<CategoryDetailDisplayPacket>();
		String sql = " select c.id as c_id,c.display_name as c_display_name,c.description as c_description "
				+ " from category c where    ( c.id in (select id from category where id in ( select id from category where category_id =?) "
				+ " or category_id in ( select category_id from category where category_id in (select id from category where category_id =?)) ) ) "
				+ " and c.status !='D' and c.locations_id = ?  ";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, categoryId).setParameter(2, categoryId)
				.setParameter(3, locationId).getResultList();
		for (Object[] objRow : resultList) {
			CategoryDetailDisplayPacket detailDisplayPacket = new CategoryDetailDisplayPacket();
			detailDisplayPacket.setId((String) objRow[0]);
			detailDisplayPacket.setCategoryDisplayName((String) objRow[1]);
			detailDisplayPacket.setDescription((String) objRow[2]);
			ans.add(detailDisplayPacket);
		}

		for (CategoryDetailDisplayPacket categoryDetailDisplayPacket : ans) {
			List<CategoryToPrinter> categoryToPrinters = getCategoryToPrinter(em, categoryDetailDisplayPacket.getId());
			List<CategoryToDiscount> categoryToDiscounts = getCategoryToDiscounts(em,
					categoryDetailDisplayPacket.getId());
			String categoryToPrinter = "";
			String categoryToDiscount = "";
			String categoryToPrinterName = "";
			String categoryToDiscountName = "";

			for (int i = 0; i < categoryToPrinters.size(); i++) {
				CategoryToPrinter categoryToPrinter2 = categoryToPrinters.get(i);
				Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em, Printer.class,
						categoryToPrinter2.getPrintersId());
				if (printer != null) {
					if (i == (categoryToPrinters.size() - 1)) {
						categoryToPrinterName += printer.getDisplayName();
						categoryToPrinter += categoryToPrinter2.getId();
					} else {
						categoryToPrinterName += printer.getDisplayName() + ",";
						categoryToPrinter += categoryToPrinter2.getId() + ",";
					}
				}
			}
			for (int i = 0; i < categoryToDiscounts.size(); i++) {
				CategoryToDiscount categoryToDiscount2 = categoryToDiscounts.get(i);
				Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
						categoryToDiscount2.getDiscountsId());
				if (discount != null) {
					if (i == (categoryToDiscounts.size() - 1)) {
						categoryToDiscountName += discount.getDisplayName();
						categoryToDiscount += categoryToDiscount2.getId();
					} else {
						categoryToDiscountName += discount.getDisplayName() + ",";
						categoryToDiscount += categoryToDiscount2.getId() + ",";
					}
				}
			}
			categoryDetailDisplayPacket.setCategoryToPrinters(categoryToPrinter);
			categoryDetailDisplayPacket.setCategoryToDiscounts(categoryToDiscount);
			categoryDetailDisplayPacket.setCategoryToPrintersName(categoryToPrinterName);
			categoryDetailDisplayPacket.setCategoryToDiscountsName(categoryToDiscountName);
		}

		return ans;
	}

	List<CategoryDetailDisplayPacket> getRawMaterialCategoryByAndLocationId(EntityManager em, String locationId)
			throws Exception {
		try {
			List<CategoryDetailDisplayPacket> ans = new ArrayList<CategoryDetailDisplayPacket>();
			String sql = " select c.id as c_id,c.display_name as c_display_name,c.description as c_description "
					+ " from category c where c.locations_id=?";

			sql += " and c.status ='R'";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();

			for (Object[] objRow : resultList) {
				CategoryDetailDisplayPacket detailDisplayPacket = new CategoryDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setCategoryDisplayName((String) objRow[1]);
				detailDisplayPacket.setDescription((String) objRow[2]);
				ans.add(detailDisplayPacket);
			}

			for (CategoryDetailDisplayPacket categoryDetailDisplayPacket : ans) {
				List<CategoryToPrinter> categoryToPrinters = getCategoryToPrinter(em,
						categoryDetailDisplayPacket.getId());
				List<CategoryToDiscount> categoryToDiscounts = getCategoryToDiscounts(em,
						categoryDetailDisplayPacket.getId());
				String categoryToPrinter = "";
				String categoryToDiscount = "";
				String categoryToPrinterName = "";
				String categoryToDiscountName = "";

				for (int i = 0; i < categoryToPrinters.size(); i++) {
					CategoryToPrinter categoryToPrinter2 = categoryToPrinters.get(i);
					Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em, Printer.class,
							categoryToPrinter2.getPrintersId());
					if (printer != null) {
						if (i == (categoryToPrinters.size() - 1)) {
							categoryToPrinterName += printer.getDisplayName();
							categoryToPrinter += categoryToPrinter2.getId();
						} else {
							categoryToPrinterName += printer.getDisplayName() + ",";
							categoryToPrinter += categoryToPrinter2.getId() + ",";
						}
					}
				}
				for (int i = 0; i < categoryToDiscounts.size(); i++) {
					CategoryToDiscount categoryToDiscount2 = categoryToDiscounts.get(i);
					Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
							categoryToDiscount2.getDiscountsId());
					if (discount != null) {
						if (i == (categoryToDiscounts.size() - 1)) {
							categoryToDiscountName += discount.getDisplayName();
							categoryToDiscount += categoryToDiscount2.getId();
						} else {
							categoryToDiscountName += discount.getDisplayName() + ",";
							categoryToDiscount += categoryToDiscount2.getId() + ",";
						}
					}
				}
				categoryDetailDisplayPacket.setCategoryToPrinters(categoryToPrinter);
				categoryDetailDisplayPacket.setCategoryToDiscounts(categoryToDiscount);
				categoryDetailDisplayPacket.setCategoryToPrintersName(categoryToPrinterName);
				categoryDetailDisplayPacket.setCategoryToDiscountsName(categoryToDiscountName);
			}
			return ans;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public List<CategoryItem> getCategoryItem(String categoryId, EntityManager em) {
		if (categoryId != null && em != null) {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CategoryItem> criteria = builder.createQuery(CategoryItem.class);
			Root<CategoryItem> r = criteria.from(CategoryItem.class);
			TypedQuery<CategoryItem> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryItem_.categoryId), categoryId),
							builder.notEqual(r.get(CategoryItem_.status), "D")));
			return query.getResultList();
		}
		return null;
	}

	List<Category> getAllCategoriesAndSubCategoriesByLocationId(EntityManager em, String locationId) {

		String queryString = "select c from Category c where c.locationsId='" + locationId
				+ "' and c.status !='D' and c.status !='I'";
		TypedQuery<Category> query = em.createQuery(queryString, Category.class);
		return query.getResultList();

	}

	int getRawMaterialCategoryCountByStatusAndLocationId(EntityManager em, String locationId, String categoryName)
			throws Exception {
		String temp = Utilities.convertAllSpecialCharForSearch(categoryName);

		String queryString = "select c from Category c where c.locationsId=? and c.displayName like '%" + temp
				+ "%' and c.status ='R'";
		TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId);
		if (query.getResultList() != null) {
			return query.getResultList().size();
		} else {
			return 0;
		}

	}

	private void manageRelationShipForItemGroup(EntityManager em, Category category, String catItemGroupId) {
		List<Item> itemList = new ArrayList<Item>();
		try {

			TypedQuery<Item> query = em
					.createQuery("SELECT i FROM Item i, CategoryItem ci WHERE ci.categoryId =? AND ci.itemsId = i.id "
							+ "and ci.status !='D' ", Item.class)
					.setParameter(1, category.getId());
			itemList = query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
			logger.severe("No CategoryItem Found for Category id" + category.getId());
		}

		if (itemList != null && itemList.size() > 0) {
			for (Item item : itemList) {
				if (item != null) {

					item.setItemGroupId(catItemGroupId);
					item = em.merge(item);

				}
			}
		}
	}

	private String getLocalItemGroup(EntityManager em, String locationId, String globalId) {
		try {
			String queryString = "select s from ItemGroup s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<ItemGroup> query = em.createQuery(queryString, ItemGroup.class).setParameter(1, globalId)
					.setParameter(2, locationId);
			return query.getSingleResult().getId();
		} catch (NoResultException e) {
			logger.severe(e);

		}
		return null;
	}

	private CatalogPacket createNewCatalogPacket(CatalogPacket old) {
		CatalogPacket packet = new CatalogPacket();
		packet.setCategory(old.getCategory());
		packet.setClientId(old.getClientId());
		packet.setDiscountsList(old.getDiscountsList());
		packet.setEchoString(old.getEchoString());
		packet.setIdOfSessionUsedByPacket(old.getIdOfSessionUsedByPacket());
		packet.setIsBaseLocationUpdate(old.getIsBaseLocationUpdate());
		packet.setLocalServerURL(old.getLocalServerURL());
		packet.setLocationId(old.getLocationId());
		packet.setLocationsListId(old.getLocationsListId());
		packet.setMerchantId(old.getMerchantId());
		packet.setPrinterList(old.getPrinterList());
		packet.setSchemaName(old.getSchemaName());
		packet.setSessionId(old.getSessionId());
		return packet;
	}

}