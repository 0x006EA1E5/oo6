import org.otherobjects.cms.util.*;

class BeanBuilderTest extends GroovyTestCase {

	Map        testMap
	Collection testCollection
	List       testList
	Object[]   testArray
	
	Object     testObject
	
	BeanBuilderBean createBean() {
		return new BeanBuilderBean();
	}
	
    void testCreateEmptyMap() {
        def builder = new BeanBuilder(this)
        Object o = builder.testMap {
        }
        assert (testMap != null)
        assert(o instanceof Map)
    }
    
    void testCreateEmptyMapAsObject() {
        def builder = new BeanBuilder(this)
        Object o = builder.testObject(class:'java.util.HashMap') {
        }
        assert (testObject != null)
        assert(testObject instanceof Map)
    }
    
       
    	
    void testCreateMapWithObjects() {
        def builder = new BeanBuilder(this)
        Object o = builder.testMap {
        		entry(key:'mike') {
        			list {
        				element(class:HashMap.class) {
        					entry(key:'mike2') {
        						value(class:ArrayList.class) {
        						}
        					}
        				}
        				map {
        					aKey {
        						list {
        						}
        					}
        				}
        			}
        		}
        }
        assert (testMap != null)
        assert(o instanceof Map)
        assert(testMap.mike != null)
        assert(testMap.mike[0].mike2 != null)
        assert(testMap.mike[1].aKey != null)
    }
    
       	
    void testCreateEmptyMapAsMap() {
        def builder = new BeanBuilder(this)
        Object o = builder.testMap {
        }
        assert (testMap != null)
        assert(o instanceof Map)
    }
    
          	
    void testCreateMapWithEntryTag() {
        def builder = new BeanBuilder(this)
        Object o = builder.testMap {
        	    entry(key:'myBean') {
      			bean {
      			}
      		}
        }
        assert (testMap != null)
        assert(o instanceof Map)
        assert (o.myBean instanceof BeanBuilderBean)
    }
    
    
          	
    void testCreateMapWithCreatorTag() {
        def builder = new BeanBuilder(this)
        Object o = builder.testMap {
        	    myBean {
      			bean {
      			}
      		}
        }
        assert (testMap != null)
        assert(o instanceof Map)
        assert (o.myBean instanceof BeanBuilderBean)
    }
    	
    void testCreateEmptyList() {
        def builder = new BeanBuilder(this)
        Object o = builder.testList {
        }
        assert (testList != null)
        assert(o instanceof List)
    }
    
    	
    void testCreateEmptyListAsObject() {
        def builder = new BeanBuilder(this)
        Object o = builder.testObject(class:'java.util.ArrayList') {
        }
        assert (testObject != null)
        assert(testObject instanceof List)
    }
    
     
       	
    void testCreateEmptyListasList() {
        def builder = new BeanBuilder(this)
        Object o = builder.testList(class:'java.util.ArrayList') {

        }
        assert (testList != null)
        assert(o instanceof List)
    }
    
    	void testCreateListWithItems() {
    		def builder = new BeanBuilder(this)
    		Object o = builder.testList {
    			map {
    				key1 {
    					map {
    						subkey1 {
    							list {
    							}
    						}
    					}
    				}
    				key2 {
    					list {
    					}
    				}
    			}
    		}
    		assert(o != null)
    		assert(testList[0].key1 instanceof Map)
    		assert(testList[0].key2 instanceof List)
    		/*
    		assert(testList[1] instanceof Collection)
    		assert(testList[1].key3 instanceof List)
    		assert(testList[1].key4 instanceof List)
    		*/
    	}
      	
    void testCreateEmptyCollection() {
        def builder = new BeanBuilder(this)
        Object o = builder.testCollection(class:ArrayList.class) {
        }
        assert (testCollection != null)
        assert(o instanceof Collection)
        assert(testCollection instanceof ArrayList)
    }
    
    	
    void testCreateEmptyCollectionAsObject() {
        def builder = new BeanBuilder(this)
        Object o = builder.testObject(class:TreeSet.class) {
        }
        assert (testObject != null)
        assert(testObject instanceof Collection)
        assert(testObject instanceof TreeSet)
    }
    
    void testCreateCollectionWithElementTag() {
        def builder = new BeanBuilder(this)
		Object o = builder.collection {
	    		element(class:BeanBuilderBean.class) {
	    		}
 		}
 		assert (o instanceof Collection)
 		assert (o[0] instanceof BeanBuilderBean)
 	}
 	
 	void testCreateCollectionWithCreatorTags() {
        def builder = new BeanBuilder(this)
        Object o = builder.collection {
			bean {
			}
		}
 		assert (o instanceof Collection)
 		assert (o[0] instanceof BeanBuilderBean)
 	}
 	
 		
 	void testCreateCollectionWithCollectionsCreatorTags() {
        def builder = new BeanBuilder(this)
        Object o = builder.bean {
        		collectionInstance {
				map {
				}
				list {
				}
				set {
				}
				collection {
				}
			}
		}
 		assert (o.collectionInstance instanceof Collection)
 		assert (o.collectionInstance[0] instanceof Map)
 		assert (o.collectionInstance[1] instanceof List)
 		assert (o.collectionInstance[2] instanceof Set)
 		assert (o.collectionInstance[3] instanceof Collection)
 	}
 	
    
           	
    void testCreateEmptyArray() {
        def builder = new BeanBuilder(this)
        Object o = builder.testArray {
        }
        assert (testArray != null)
    }
    
              	
    void testCreateArray() {
        def builder = new BeanBuilder(this)
        Object o = builder.testArray {
        		map {
        			key {
        				list {
        				}
        			}
        		}
        		map {
        			key {
        				list {
        				}
        			}
        		}
        }
        assert (testArray != null)
        assert (testArray[0] != null)
        assert (testArray[0].key != null)
        
    }
    
    void testCreateEmptyBean() {
        def builder = new BeanBuilder(this)
        Object o = builder.bean {
        }
        assert (o != null)
        assert (o instanceof BeanBuilderBean)
     }
     
        
    void testCreateBean() {
        def builder = new BeanBuilder(this)
        Object o = builder.bean {
        		beanInstance {
        			mapInstance {
        				key1 {
        					bean {
        					}
        				}
        				key2 {
        					list {
        					}
        				}
        			}
        		}
        		bean {
        			setInstance {
        				list {
        				}
        				map {
        				}
        			}
        		}
        }
        assert (o != null)
        assert (o instanceof BeanBuilderBean)
        assert (o.beanInstance instanceof BeanBuilderBean)
        assert (o.beanInstance.mapInstance instanceof Map)
        assert (o.beanInstance.mapInstance.key1 instanceof BeanBuilderBean)
        assert (o.beanInstance.mapInstance.key2 instanceof List)
        assert (o.beanList.size() > 0)
     }
     
     public void testSimpleBeanProperties() {
     	def builder = new BeanBuilder(this);
		Object o = builder.bean {
			objectInstance(class:BeanBuilderBean.class, stringInstance: 'hello world') {
			}
		}
		assert (o != null)
		assert (o.objectInstance instanceof BeanBuilderBean)
		assert (o.objectInstance.stringInstance == 'hello world')
	}
	
	public void testSharedBeanReferences() {
     	def builder = new BeanBuilder(this);
		Object o = builder.bean {
			beanInstance(bbname:'shared-bean') {
				listInstance {
					map {
						key {
							bean(bbnameref:'shared-bean')
						}
					}
				}
			}
			objectInstance(bbnameref:'shared-bean')
		}
		
		assert (o != null)
		assert (o.beanInstance != null)
		assert (o.beanInstance.listInstance[0].key != null)
		assert (o.beanInstance == o.beanInstance.listInstance[0].key)
		assert (o.objectInstance != null)
		assert (o.objectInstance == o.beanInstance)
		assert (o.objectInstance == o.beanInstance.listInstance[0].key)
		
	}
    	
    
}
