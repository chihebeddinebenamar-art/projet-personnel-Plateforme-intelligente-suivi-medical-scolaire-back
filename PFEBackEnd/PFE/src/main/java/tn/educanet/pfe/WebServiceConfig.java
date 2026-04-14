package tn.educanet.pfe;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

@Configuration
@EnableWs
@ComponentScan(basePackages = "tn.educanet.pfe.endpoint")
public class WebServiceConfig extends WsConfigurerAdapter {

	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
			ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean<>(servlet, "/soap/*");
	}

	@Bean(name = "educanet")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchemaCollection schemaCollection) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("init");
		wsdl11Definition.setLocationUri("/soap");
		wsdl11Definition.setTargetNamespace("http://www.educanet.tn.com/business/wsdl");
		wsdl11Definition.setSchemaCollection(schemaCollection);
		return wsdl11Definition;
	}

	@Bean
	@Primary
	public DozerBeanMapper dozerBeanMapperFactoryBean() {
		DozerBeanMapper factoryBean = new DozerBeanMapper();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			Resource[] resources = resolver.getResources("classpath:dozer/*.xml");

			List<String> mappingFiles = new ArrayList<String>();
			for (Resource resource : resources) {
				System.out.println("Mapping files : " + resource.getURI().toString());
				mappingFiles.add(resource.getURI().toString());
			}

			factoryBean.setMappingFiles(mappingFiles);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return factoryBean;
	}

	@Bean
	public XsdSchemaCollection schemaCollection() {
		CommonsXsdSchemaCollection collection = new CommonsXsdSchemaCollection(
				new ClassPathResource[] { 
						
				            new ClassPathResource("xsd/user.xsd")

				});
		collection.setInline(true);
		return collection;
	}
}
