package org.otherobjects.cms.groovy;

import com.eaio.uuid.UUID
import java.text.SimpleDateFormat
import org.otherobjects.cms.util.StringUtils

class GeneratedDataTransformer {

  static void main(args) {
	def writer = new StringWriter()
	def builder = new groovy.xml.MarkupBuilder(writer)
	
	def writeRecord = {record,index ->
		def ccfullname = "${record.firstname.text()} ${record.surname.text()}"
		def code = StringUtils.generateUrlCode(ccfullname) + ".html" // same as in SitePage.getCode()
		builder."$code"(fullname: ccfullname,firstname:record.firstname.text(), 
			surname:record.surname.text(),city:record.city.text(), 
			zip:record.zip.text(),email:record.email.text(),dob:getDateinProperFormat(record.dob.text()),
			'jcr:primaryType':'oo:node', 'jcr:uuid':new UUID(), ooType:'TestPerson','ocm:classname':'TestPerson')
	}
	
	def records = new  XmlParser().parse(new File('/users/joerg/downloads/randomdata.xml'))
	def outrecords = builder.persons('jcr:primaryType':'oo:node', 'jcr:uuid':new UUID(),ooType:"org.otherobjects.cms.model.SiteFolder",label:'Persons','ocm:classname':"org.otherobjects.cms.model.SiteFolder") {
		records.record.eachWithIndex(writeRecord)
	}

	println writer
  }
  
  static String getDateinProperFormat(datestring)
  {
	  def ipfmt = new SimpleDateFormat("dd-MM-yyyy")
	  
	  return ipfmt.parse(datestring).getTime() + ""
  }

}