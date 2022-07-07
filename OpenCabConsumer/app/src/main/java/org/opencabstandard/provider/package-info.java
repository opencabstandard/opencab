/**
 * Java interface files that define the contracts for OpenCab <i>providers</i>.
 * 
 * <h3>Overview</h3>
 * 
 * An OpenCab <i>provider</i>
 * is a mechanism for communicating between two Android apps running on the same device.  One app will be the
 * OpenCab provider app and will implement an Android
 * <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * that implements the appropriate Contract class.  The second app will be the OpenCab consumer app and will
 * make calls to the
 * <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * supplied by the provider app.
 *
 * <p>
 *     The Contract and Abstract classes can be found
 *     <a href="https://github.com/opencabstandard/opencab/tree/master/OpenCabProvider/app/src/main/java/org/opencabstandard/provider">here</a>.
 *     You will want to add the relevant classes to your Android project.
 * </p>
 *
 * <p>
 * For example, an app that will be an OpenCab Identity provider will contain a
 * <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * that implements the {@link org.opencabstandard.provider.IdentityContract}.  The OpenCab consumer app will
 * make calls to the
 * <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * supplied by the OpenCab provider app.  In this example the consumer app may use the results from
 * <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * to SSO into the consumer app with credentials from the provider app.
 * </p>
 *
 * <p>
 *     To aid in implementing the
 *     <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>,
 *     we have created abstract implementations that handle much of the work for you.  For the Identity provider,
 *     you may extend the {@link org.opencabstandard.provider.AbstractIdentityProvider} class as follows:
 *
 * <pre>
 * <code class="language-java">
 *     public class MyIdentityProvider extends AbstractIdentityProvider {
 *          {@literal @}Override
 *          public IdentityContract.LoginCredentials getLoginCredentials(String version) {
 *              // Your logic goes here
 *          }
 *
 *          {@literal @}Override
 *          public ArrayList&lt;IdentityContract.Driver&gt; getActiveDrivers(String version) {
 *              // Your logic goes here
 *          }
 *     }
 * </code>
 * </pre>
 *
 * <p>
 *     An example sequence might be like the following:
 * </p>
 * <div class="mermaid">
 *     sequenceDiagram
 *       participant A as OpenCab Consumer
 *       participant B as OpenCab Provider
 *       participant C as Consumer Auth Service
 *       A-&gt;&gt;+B: provider.call(METHOD_GET_LOGIN_TOKEN, version: 1)
 *       B-&gt;&gt;A: loginToken
 *       A-&gt;&gt;C: Authentication Request w/loginToken
 *       C-&gt;&gt;A: Authentication Response
 * </div>
 * <h3>Security</h3>
 * <p>
 * The OpenCab provider app must declare the ContentProviders in the manifest with dual authorities.  One
 * authority must match the OpenCab AUTHORITY in the appropriate Contract class and the second should
 * be unique to the provider application.  The OpenCab consumer app will identify the
 * <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a> based
 * on the OpenCab AUTHORITY, but then will use the second authority to make the call to the
 * <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>.
 * </p>
 * <p>An example of the ContentProvider declared in the manifest:</p>
 * <pre class="language-javascript">
 * {@code
 *         <provider android:authorities="org.opencabstandard.identity;com.myexample.identity"
 *             android:exported="true"
 *             android:label="identity"
 *             android:name="com.myexample.IdentityContentProvider">
 *         </provider>
 * }
 * </pre>
 *
 * The OpenCab consumer app must register the OpenCab authorities as queries in the Android manifest as follows:
 * <pre class="language-javascript">
 * {@code
 *     <queries>
 *         <provider android:authorities="org.opencabstandard.hos" />
 *         <provider android:authorities="org.opencabstandard.identity" />
 *     </queries>
 * }
 * </pre>
 *
 */
package org.opencabstandard.provider;
