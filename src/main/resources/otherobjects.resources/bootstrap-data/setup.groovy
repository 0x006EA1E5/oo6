def dao = daoService.getDao("DynaNode");

// Create base structure
def site = dao.create("org.otherobjects.cms.model.SiteFolder");
site.jcrPath="/site";
site.label="\${site.label}";
site.cssClass="site";
//site.id="ed587d28-eee9-4c6d-8887-a1b5332ca262";
site = dao.save(site);
dao.publish(site);

def libraries = dao.create("org.otherobjects.cms.model.SiteFolder");
libraries.jcrPath="/libraries";
libraries.label="\${libraries.label}";
libraries.cssClass="libraries";
//libraries.id="988cb444-7179-4303-9c41-0a45c27083e4";
dao.save(libraries);
dao.publish(libraries);

def images = dao.create("org.otherobjects.cms.model.SiteFolder");
images.jcrPath="/libraries/images";
images.label="\${images.label}";
dao.save(images);
dao.publish(images);

def designer = dao.create("org.otherobjects.cms.model.SiteFolder");
designer.jcrPath="/designer";
designer.label="\${designer.label}";
designer.cssClass="designer";
//trash.id="69259992-e7a6-4acf-9170-af77478a295d";
designer = dao.save(designer);
dao.publish(designer);

def trash = dao.create("org.otherobjects.cms.model.SiteFolder");
trash.jcrPath="/trash";
trash.label="\${trash.label}";
trash.cssClass="trash";
//trash.id="e6399253-140c-48f6-9f73-dd79fe1b628f";
dao.save(trash);
dao.publish(trash);

// Move pre-created data types to proper location
def types = dao.getByPath("/types");
assert types : "Could not find types folder";
dao.moveItem(types.id, designer.id, "below");
dao.publish(types);