/**
 * Copyright 2005-2009 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package org.restlet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ClientInfo;
import org.restlet.data.Conditions;
import org.restlet.data.Cookie;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Range;
import org.restlet.data.Reference;
import org.restlet.engine.util.CookieSeries;
import org.restlet.representation.Representation;
import org.restlet.util.Series;

/**
 * Generic request sent by client connectors. It is then received by server
 * connectors and processed by Restlets. This request can also be processed by a
 * chain of Restlets, on both client and server sides. Requests are uniform
 * across all types of connectors, protocols and components.
 * 
 * @see org.restlet.Response
 * @see org.restlet.Uniform
 * @author Jerome Louvel
 */
public class Request extends Message {

    // [ifndef gwt] method
    /**
     * Returns the request associated to the current thread. This is reusing the
     * {@link Response#getCurrent()} method.
     * 
     * Warning: this method should only be used under duress. You should by
     * default prefer obtaining the current context using methods such as
     * {@link org.restlet.resource.Resource#getRequest()}.
     * 
     * @return The thread's request.
     */
    public static Request getCurrent() {
        return (Response.getCurrent() == null) ? null : Response.getCurrent()
                .getRequest();
    }

    /** The authentication response sent by a client to an origin server. */
    private volatile ChallengeResponse challengeResponse;

    // [ifndef gwt] member
    /** The authentication response sent by a client to a proxy. */
    private volatile ChallengeResponse proxyChallengeResponse;

    /** The client-specific information. */
    private volatile ClientInfo clientInfo;

    /** The condition data. */
    private volatile Conditions conditions;

    /** The cookies provided by the client. */
    private volatile Series<Cookie> cookies;

    /** The host reference. */
    private volatile Reference hostRef;

    /** The protocol. */
    private volatile Protocol protocol;

    /** The method. */
    private volatile Method method;

    /** The original reference. */
    private volatile Reference originalRef;

    /** The ranges to return from the target resource's representation. */
    private volatile List<Range> ranges;

    /** The referrer reference. */
    private volatile Reference referrerRef;

    /** The resource reference. */
    private volatile Reference resourceRef;

    /** The application root reference. */
    private volatile Reference rootRef;

    /**
     * Constructor.
     */
    public Request() {
    }

    /**
     * Constructor.
     * 
     * @param method
     *            The call's method.
     * @param resourceRef
     *            The resource reference.
     */
    public Request(Method method, Reference resourceRef) {
        this(method, resourceRef, null);
    }

    /**
     * Constructor.
     * 
     * @param method
     *            The call's method.
     * @param resourceRef
     *            The resource reference.
     * @param entity
     *            The entity.
     */
    public Request(Method method, Reference resourceRef, Representation entity) {
        super(entity);
        this.challengeResponse = null;
        this.clientInfo = null;
        this.conditions = null;
        this.cookies = null;
        this.hostRef = null;
        this.method = method;
        this.originalRef = null;
        // [ifndef gwt] instruction
        this.proxyChallengeResponse = null;
        this.protocol = null;
        this.ranges = null;
        this.referrerRef = null;
        this.resourceRef = resourceRef;
        this.rootRef = null;
    }

    /**
     * Constructor.
     * 
     * @param method
     *            The call's method.
     * @param resourceUri
     *            The resource URI.
     */
    public Request(Method method, String resourceUri) {
        this(method, new Reference(resourceUri));
    }

    /**
     * Constructor.
     * 
     * @param method
     *            The call's method.
     * @param resourceUri
     *            The resource URI.
     * @param entity
     *            The entity.
     */
    public Request(Method method, String resourceUri, Representation entity) {
        this(method, new Reference(resourceUri), entity);
    }

    /**
     * Asks the server connector to immediately commit the given response
     * associated to this request, making it ready to be sent back to the
     * client. Note that all server connectors don't necessarily support this
     * feature.
     */
    public synchronized void commit(Response response) {
    }

    /**
     * Returns the authentication response sent by a client to an origin server.
     * Note that when used with HTTP connectors, this property maps to the
     * "Authorization" header.
     * 
     * @return The authentication response sent by a client to an origin server.
     */
    public ChallengeResponse getChallengeResponse() {
        return this.challengeResponse;
    }

    /**
     * Returns the client-specific information. Creates a new instance if no one
     * has been set.
     * 
     * @return The client-specific information.
     */
    public ClientInfo getClientInfo() {
        // Lazy initialization with double-check.
        ClientInfo c = this.clientInfo;
        if (c == null) {
            synchronized (this) {
                c = this.clientInfo;
                if (c == null) {
                    this.clientInfo = c = new ClientInfo();
                }
            }
        }
        return c;
    }

    /**
     * Returns the modifiable conditions applying to this request. Creates a new
     * instance if no one has been set.
     * 
     * @return The conditions applying to this call.
     */
    public Conditions getConditions() {
        // Lazy initialization with double-check.
        Conditions c = this.conditions;
        if (c == null) {
            synchronized (this) {
                c = this.conditions;
                if (c == null) {
                    this.conditions = c = new Conditions();
                }
            }
        }
        return c;
    }

    /**
     * Returns the modifiable series of cookies provided by the client. Creates
     * a new instance if no one has been set.<br>
     * <br>
     * Note that when used with HTTP connectors, this property maps to the
     * "Cookie" header.
     * 
     * @return The cookies provided by the client.
     */
    public Series<Cookie> getCookies() {
        // Lazy initialization with double-check.
        Series<Cookie> c = this.cookies;
        if (c == null) {
            synchronized (this) {
                c = this.cookies;
                if (c == null) {
                    this.cookies = c = new CookieSeries();
                }
            }
        }
        return c;
    }

    /**
     * Returns the host reference. This may be different from the resourceRef's
     * host, for example for URNs and other URIs that don't contain host
     * information.<br>
     * <br>
     * Note that when used with HTTP connectors, this property maps to the
     * "Host" header.
     * 
     * @return The host reference.
     */
    public Reference getHostRef() {
        return this.hostRef;
    }

    /**
     * Returns the method.
     * 
     * @return The method.
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * Returns the original reference as requested by the client. Note that this
     * property is not used during request routing. See the
     * {@link #getResourceRef()} method for details.
     * 
     * @return The original reference.
     * @see #getResourceRef()
     */
    public Reference getOriginalRef() {
        return this.originalRef;
    }

    /**
     * Returns the protocol used or to be used, if known.
     * 
     * @return The protocol used or to be used.
     */
    public Protocol getProtocol() {
        Protocol result = this.protocol;

        if ((result == null) && (getResourceRef() != null)) {
            // Attempt to guess the protocol to use
            // from the target reference scheme
            result = getResourceRef().getSchemeProtocol();
            // Fallback: look at base reference scheme
            if (result == null) {
                result = (getResourceRef().getBaseRef() != null) ? getResourceRef()
                        .getBaseRef().getSchemeProtocol()
                        : null;
            }
        }

        return result;
    }

    // [ifndef gwt] method
    /**
     * Returns the authentication response sent by a client to a proxy. Note
     * that when used with HTTP connectors, this property maps to the
     * "Proxy-Authorization" header.
     * 
     * @return The authentication response sent by a client to a proxy.
     */
    public ChallengeResponse getProxyChallengeResponse() {
        return this.proxyChallengeResponse;
    }

    /**
     * Returns the ranges to return from the target resource's representation.
     * Note that when used with HTTP connectors, this property maps to the
     * "Range" header.
     * 
     * @return The ranges to return.
     */
    public List<Range> getRanges() {
        // Lazy initialization with double-check.
        List<Range> r = this.ranges;
        if (r == null) {
            synchronized (this) {
                r = this.ranges;
                if (r == null) {
                    this.ranges = r = new CopyOnWriteArrayList<Range>();
                }
            }
        }
        return r;
    }

    /**
     * Returns the referrer reference if available. Note that when used with
     * HTTP connectors, this property maps to the "Referer" header.
     * 
     * @return The referrer reference.
     */
    public Reference getReferrerRef() {
        return this.referrerRef;
    }

    /**
     * Returns the reference of the target resource. This reference is
     * especially important during routing, dispatching and resource finding.
     * During such processing, its base reference is constantly updated to
     * reflect the reference of the parent Restlet or resource and the remaining
     * part of the URI that must be routed or analyzed.
     * 
     * If you need to get the URI reference originally requested by the client,
     * then you should use the {@link #getOriginalRef()} method instead. Also,
     * note that beside the update of its base property, the resource reference
     * can be modified during the request processing.
     * 
     * For example, the {@link org.restlet.service.TunnelService} associated to
     * an application can extract some special extensions or query parameters
     * and replace them by semantically equivalent properties on the request
     * object. Therefore, the resource reference can become different from the
     * original reference.
     * 
     * Finally, when sending out requests via a dispatcher such as
     * {@link Context#getClientDispatcher()} or
     * {@link Context#getServerDispatcher()}, if the reference contains URI
     * template variables, those variables are automatically resolved using the
     * request's attributes.
     * 
     * @return The reference of the target resource.
     * @see #getOriginalRef()
     * @see #getHostRef()
     */
    public Reference getResourceRef() {
        return this.resourceRef;
    }

    /**
     * Returns the application root reference.
     * 
     * @return The application root reference.
     */
    public Reference getRootRef() {
        return this.rootRef;
    }

    /**
     * Implemented based on the {@link Protocol#isConfidential()} method for the
     * request's protocol returned by {@link #getProtocol()};
     */
    @Override
    public boolean isConfidential() {
        return (getProtocol() == null) ? false : getProtocol().isConfidential();
    }

    /**
     * Indicates if a content is available and can be sent. Several conditions
     * must be met: the method must allow the sending of content, the content
     * must exists and have some available data.
     * 
     * @return True if a content is available and can be sent.
     */
    @Override
    public boolean isEntityAvailable() {
        // The declaration of the "result" variable is a workaround for the GWT
        // platform.
        boolean result = (Method.GET.equals(getMethod())
                || Method.HEAD.equals(getMethod()) || Method.DELETE
                .equals(getMethod()));
        if (result) {
            return false;
        }

        return super.isEntityAvailable();
    }

    /**
     * Sets the authentication response sent by a client to an origin server.
     * Note that when used with HTTP connectors, this property maps to the
     * "Authorization" header.
     * 
     * @param challengeResponse
     *            The authentication response sent by a client to an origin
     *            server.
     */
    public void setChallengeResponse(ChallengeResponse challengeResponse) {
        this.challengeResponse = challengeResponse;
    }

    /**
     * Sets the client-specific information.
     * 
     * @param clientInfo
     *            The client-specific information.
     */
    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * Sets the conditions applying to this request.
     * 
     * @param conditions
     *            The conditions applying to this request.
     */
    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    /**
     * Sets the cookies provided by the client. Note that when used with HTTP
     * connectors, this property maps to the "Cookie" header.
     * 
     * @param cookies
     *            The cookies provided by the client.
     */
    public void setCookies(Series<Cookie> cookies) {
        this.cookies = cookies;
    }

    /**
     * Sets the host reference. Note that when used with HTTP connectors, this
     * property maps to the "Host" header.
     * 
     * @param hostRef
     *            The host reference.
     */
    public void setHostRef(Reference hostRef) {
        this.hostRef = hostRef;
    }

    /**
     * Sets the host reference using an URI string. Note that when used with
     * HTTP connectors, this property maps to the "Host" header.
     * 
     * @param hostUri
     *            The host URI.
     */
    public void setHostRef(String hostUri) {
        setHostRef(new Reference(hostUri));
    }

    /**
     * Sets the method called.
     * 
     * @param method
     *            The method called.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Sets the original reference requested by the client.
     * 
     * @param originalRef
     *            The original reference.
     * @see #getOriginalRef()
     */
    public void setOriginalRef(Reference originalRef) {
        this.originalRef = originalRef;
    }

    /**
     * Sets the protocol used or to be used.
     * 
     * @param protocol
     *            The protocol used or to be used.
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    // [ifndef gwt] method
    /**
     * Sets the authentication response sent by a client to a proxy. Note that
     * when used with HTTP connectors, this property maps to the
     * "Proxy-Authorization" header.
     * 
     * @param challengeResponse
     *            The authentication response sent by a client to a proxy.
     */
    public void setProxyChallengeResponse(ChallengeResponse challengeResponse) {
        this.proxyChallengeResponse = challengeResponse;
    }

    /**
     * Sets the ranges to return from the target resource's representation. Note
     * that when used with HTTP connectors, this property maps to the "Range"
     * header.
     * 
     * @param ranges
     *            The ranges.
     */
    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }

    /**
     * Sets the referrer reference if available. Note that when used with HTTP
     * connectors, this property maps to the "Referer" header.
     * 
     * @param referrerRef
     *            The referrer reference.
     */
    public void setReferrerRef(Reference referrerRef) {
        this.referrerRef = referrerRef;

        // A referrer reference must not include a fragment.
        if ((this.referrerRef != null)
                && (this.referrerRef.getFragment() != null)) {
            this.referrerRef.setFragment(null);
        }
    }

    /**
     * Sets the referrer reference if available using an URI string. Note that
     * when used with HTTP connectors, this property maps to the "Referer"
     * header.
     * 
     * @param referrerUri
     *            The referrer URI.
     * @see #setReferrerRef(Reference)
     */
    public void setReferrerRef(String referrerUri) {
        setReferrerRef(new Reference(referrerUri));
    }

    /**
     * Sets the target resource reference. If the reference is relative, it will
     * be resolved as an absolute reference. Also, the context's base reference
     * will be reset. Finally, the reference will be normalized to ensure a
     * consistent handling of the call.
     * 
     * @param resourceRef
     *            The resource reference.
     * @see #getResourceRef()
     */
    public void setResourceRef(Reference resourceRef) {
        this.resourceRef = resourceRef;
    }

    /**
     * Sets the target resource reference using an URI string. Note that the URI
     * can be either absolute or relative to the context's base reference.
     * 
     * @param resourceUri
     *            The resource URI.
     * @see #setResourceRef(Reference)
     */
    public void setResourceRef(String resourceUri) {
        if (getResourceRef() != null) {
            // Allow usage of URIs relative to the current base reference
            setResourceRef(new Reference(getResourceRef().getBaseRef(),
                    resourceUri));
        } else {
            setResourceRef(new Reference(resourceUri));
        }
    }

    /**
     * Sets the application root reference.
     * 
     * @param rootRef
     *            The application root reference.
     */
    public void setRootRef(Reference rootRef) {
        this.rootRef = rootRef;
    }

}
