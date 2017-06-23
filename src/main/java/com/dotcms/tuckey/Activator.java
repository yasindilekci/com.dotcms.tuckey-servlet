package com.dotcms.tuckey;

import javax.servlet.Servlet;

import org.apache.felix.http.api.ExtHttpService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.dotcms.repackage.org.tuckey.web.filters.urlrewrite.NormalRule;
import com.dotcms.repackage.org.tuckey.web.filters.urlrewrite.SetAttribute;
import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.osgi.GenericBundleActivator;

public class Activator extends GenericBundleActivator {

	private Servlet simpleServlet;
	private ExtHttpService httpService;

    @Override
	@SuppressWarnings ("unchecked")
    public void start ( BundleContext context ) throws Exception {
        //Initializing services...
        initializeServices( context );

        //Service reference to ExtHttpService that will allows to register servlets and filters
        ServiceReference sRef = context.getServiceReference( ExtHttpService.class.getName() );

        if ( sRef != null ) {

            httpService = (ExtHttpService) context.getService( sRef );
            try {
                //Registering a simple test servlet
                simpleServlet = new HelloWorldServlet( );
                httpService.registerServlet( "/helloworld", simpleServlet, null, null );

            } catch ( Exception e ) {
                e.printStackTrace();
            }

        }

        CMSFilter.excludeURI( "/app/helloworld" );

        addRewriteRule(".*servlet-test$", "/app/helloworld", "forward", "ExampleServlet1");

        addRewriteRule(".*image-forward", "/html/images/backgrounds/bg-6.jpg", "forward", "ExampleImage1");

        // To forward to a dotCMS served resource, you need to
        // set the request Parameter CMSFilter.CMS_FILTER_URI_OVERRIDE.
        // This allows you to specify a forwarding page in dotCMS

        NormalRule forwardRule = new NormalRule();
        forwardRule.setFrom( ".*2servlet-test$" );
        SetAttribute attribute = new SetAttribute();
        attribute.setName(CMSFilter.CMS_FILTER_URI_OVERRIDE);
        attribute.setValue("/app/helloworld");
        forwardRule.addSetAttribute(attribute);
        addRewriteRule( forwardRule );

    }

    @Override
	public void stop ( BundleContext context ) throws Exception {

        //Unregister all the bundle services
        unregisterServices( context );
    }

}