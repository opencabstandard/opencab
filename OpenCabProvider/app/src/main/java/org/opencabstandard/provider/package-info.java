/**
 * This package contains the Java interface files that define the contracts for OpenCab
 * <i>providers</i>, the data types communicated to and from those providers, as well as abstract
 * implementations that reduce the boilerplate code needed for an implementation.
 *
 * <p>You can <a href="https://github.com/opencabstandard/opencab/edit/master/OpenCabProvider/app/src/main/java/org/opencabstandard/provider/package-info.java">propose changes or improvements to this documentation using GitHub.</a>
 *
 * <h2>Overview</h2>
 *
 * <p>An OpenCab <i>provider</i> is a mechanism for communicating between two Android apps running
 * on the same device. One app will act as the OpenCab provider app and will contain an Android <a
 * href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * that implements the appropriate Contract class. The second app acts as the OpenCab consumer app
 * and will make calls to the <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * supplied by the provider app.</p>
 *
 * <p> The Contract and Abstract classes can be found <a href="https://github.com/opencabstandard/opencab/tree/master/OpenCabProvider/app/src/main/java/org/opencabstandard/provider">in
 * source code for this package</a>. You should add these relevant classes to your Android
 * project.</p>
 *
 * <p> For example, an app that will be an OpenCab Identity provider will contain a <a
 * href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * that implements the {@link org.opencabstandard.provider.IdentityContract}. The OpenCab consumer
 * app will make calls to the <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * supplied by the OpenCab provider app. In this example the consumer app may use the results from
 * <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * to SSO into the consumer app with credentials from the provider app. </p>
 *
 * <p> To aid in implementing the <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>,
 * we have created abstract implementations that handle much of the work for you. For the Identity
 * provider, you may extend the {@link org.opencabstandard.provider.AbstractIdentityProvider} class
 * as follows:
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
 * An example sequence might be like the following:
 * </p>
 * <div class="mermaid">
 *     sequenceDiagram
 *       participant A as OpenCab Consumer
 *       participant B as OpenCab Provider
 *       participant C as Consumer Auth Service
 *       A-&gt;&gt;+B: provider.call(METHOD_GET_LOGIN_CREDENTIALS, version: "0.2")
 *       B-&gt;&gt;A: KEY_VERSION="0.2" <br>KEY_LOGIN_CREDENTIALS=LoginCredentials{ authority="example" token="kf40m1fpl…d28zckhuf6" … }
 *       A-&gt;&gt;C: Authentication request with token
 *       C-&gt;&gt;A: Authentication response
 * </div>
 *
 * <h3>Package visibility</h3>
 * <p><b>Note: Provider declaration has been updated from previous versions of the specification to
 * support multiple provider apps installed at once.</b> See the section on "Backwards compatibility
 * with older versions" for details about how to support providers using the older approach.</p>
 *
 * <p>In order to allow provider and consumer apps to communicate with each other on <a
 * href="https://developer.android.com/training/package-visibility">modern Android versions</a>,
 * participating apps must include certain declarations in <code>AndroidManifest.xml</code>. The
 * specific declarations needed for providers and consumers are discussed in detail below.</p>
 *
 * <h3>Providers</h3>
 *
 * <h4>Base requirements</h4>
 *
 * <p>Providers MUST declare an exported Android <code>Service</code> that handles the Intent action
 * <code>org.opencabstandard.PROVIDER</code>:</p>
 *
 * <pre class="language-xml"> {@code
 *   <service android:name=".OpenCabProviderService" android:exported="true">
 *     <intent-filter>
 *       <action android:name="org.opencabstandard.PROVIDER" />
 *     </intent-filter>
 *   </service>
 * } </pre>
 *
 * <p>The implementation of <code>OpenCabProviderService</code> SHOULD be empty:</p>
 *
 * <pre> <code class="language-java">
 * public class OpenCabProviderService extends IntentService {
 *   public OpenCabProviderService() { super("OpenCabProviderService"); }
 *
 *   {@literal @}Override protected void onHandleIntent(Intent intent) { // No-op }
 * }
 * </code> </pre>
 *
 * <p>This service allows consumer implementations to declare their dependency on the provider app
 * to Android, which then permits the consumer apps to see and interact with the provider
 * package.</p>
 *
 * <h4>For each implemented contract</h4>
 *
 * <p>For each contract, providers MUST declare their content providers using a unique authority
 * formed using the app's package name. The format for this authority is <code>{app package
 * name}.{OpenCab authority}</code>. For example, for the app <code>com.example.app</code>
 * implementing the vehicle information provider contract, the following
 * <code>AndroidManifest.xml</code> section would be necessary:</p>
 *
 * <pre class="language-xml"> {@code
 * <provider android:authorities="com.example.app.org.opencabstandard.vehicleinformation"
 *           android:exported="true"
 *           android:label="VehicleInformation"
 *           android:name="com.example.app.opencab.VehicleInformationProvider">
 * </provider>
 * } </pre>
 *
 * <p>The package name prefix makes the authority unique among other providers installed on the same
 * device, and the standard suffix makes the authority discoverable by potential consumer apps.</p>
 *
 * <p>Providers MUST NOT declare an <code>android:authorities</code> attribute containing any of the
 * <code>org.opencabstandard.*</code> authority strings . This is a change from previous versions of
 * the specification—for details, see "Backwards compatibility with older versions."</p>
 *
 * <h4>For broadcast intents</h4>
 *
 * <p>Providers that implement a contract (such as IdentityProvider) including broadcast intents
 * MUST declare their desire to publish these events to Android using a <code>queries</code> block
 * in <code>AndroidManifest.xml</code>:</p>
 *
 * <pre class="language-xml"> {@code
 *  <queries>
 *    <intent>
 *      <action android:name="IdentityContract.ACTION_IDENTITY_INFORMATION_CHANGED" />
 *    </intent>
 *  </queries>
 * } </pre>
 *
 * <p>This block signals to Android's internal package visibility protections that the provider
 * intends to explicitly publish this event to other apps.</p>
 *
 * <p>This block can be repeated as needed for each broadcast intent.</p>
 *
 * <p>For an implementation example, see the <a href="https://github.com/opencabstandard/opencab/blob/master/OpenCabProvider/app/src/main/AndroidManifest.xml">sample
 * provider app</a>.</p>
 *
 * <h3>Consumers</h3>
 *
 * <h4>Base requirements</h4>
 *
 * <p>All consumer apps MUST indicate their participation in OpenCab with the following block in
 * <code>AndroidManifest.xml</code>:</p>
 *
 * <pre class="language-xml"> {@code <queries>
 *   <intent>
 *     <action android:name="org.opencabstandard.PROVIDER" />
 *   </intent>
 * </queries> }</pre>
 *
 * <p>Consumer apps MAY enumerate and select provider(s) based on enumeration (using the Android
 * <code>PackageManager</code> class) or runtime configuration.</p>
 *
 * <h4>For broadcast intents</h4>
 *
 * <p>Consumer implementations MAY choose to register Android receivers for one or more broadcast
 * intents. The declared receiver MUST list the names of the action(s) corresponding to the desired
 * events. For example, an app that consumes identity information using the
 * <code>IdentityContract</code> and wishes to receive an explicit broadcast intent upon login or
 * logout might include the following in its <code>AndroidManifest.xml</code>:</p>
 *
 * <pre class="language-xml"> {@code
 * <receiver android:name="IdentityChangedReceiver"
 *           android:enabled="true"
 *           android:exported="true">
 *   <intent-filter>
 *     <action android:name="IdentityContract.ACTION_DRIVER_LOGIN" />
 *     <action android:name="IdentityContract.ACTION_DRIVER_LOGOUT" />
 *   </intent-filter>
 * </receiver>
 * }</pre>
 *
 * <p>For an implementation example, see the <a href="https://github.com/opencabstandard/opencab/blob/master/OpenCabConsumer/app/src/main/AndroidManifest.xml">sample
 * consumer app</a>.</p>
 *
 * <h3>Security</h3>
 * <p>At this time, limiting the exchange of data with another OpenCab-enabled app for reasons of
 * privacy or security is left up to individual implementations. Providers and consumers MAY use
 * OS-level mechanisms (such as <a href="https://developer.android.com/reference/android/content/ContentProvider#getCallingPackage()">ContentProvider.getCallingPackage()</a>)
 * to limit the exchange of data to packages that have been specifically permitted by an end user or
 * customer.</p>
 *
 * <h3>Backwards compatibility with older versions</h3>
 *
 * <p>Previous versions of the specification required apps to declare their <a
 * href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * with a specific <code>android:authorities</code> attribute containing a value such as
 * <code>org.opencabstandard.identity</code>. Specifically, the specification read:
 *
 * <blockquote>
 * <p> The OpenCab provider app must declare the ContentProviders in the manifest with dual
 * authorities. One authority must match the OpenCab AUTHORITY in the appropriate Contract class and
 * the second should be unique to the provider application. The OpenCab consumer app will identify
 * the <a href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>
 * based on the OpenCab AUTHORITY, but then will use the second authority to make the call to the <a
 * href="https://developer.android.com/reference/android/content/ContentProvider">ContentProvider</a>.
 * </p> <p>An example of the ContentProvider declared in the manifest:</p>
 *
 * <pre class="language-xml"> {@code
 * <provider android:authorities="org.opencabstandard.identity;com.myexample.identity"
 *           android:exported="true"
 *           android:label="identity"
 *           android:name="com.myexample.IdentityContentProvider"></provider>
 * } </pre>
 * </blockquote>
 * <p>
 * However, since this configuration only allows one provider app of each type (identity, HOS, etc.)
 * to be installed on a device at once, this style of declaration deprecated and <b>no longer
 * permitted.</b> Provider implementations MUST NOT include one of the
 * <code>org.opencabstandard.*</code> authorities. However, to enable newer consumer implementations
 * to interoperate with older providers that predate this change, consumer implementations SHOULD
 * fall back to enumerating and selecting a provider declaring one of these authorities. Consumer
 * implementations MUST prefer a provider declared using the new, unique authority to one declared
 * using the older format.
 * <p>
 * Consumer implementations SHOULD continue to include <code>queries</code> declarations in the
 * Android manifest so that apps with an older provider implementation remain visible under the new
 * Android package visibility rules. For example:
 *
 * <pre class="language-xml"> {@code
 *   <queries>
 *     <provider android:authorities="org.opencabstandard.hos" />
 *     <provider android:authorities="org.opencabstandard.identity" />
 *   </queries>
 * } </pre>
 *
 * <p>For a complete example, see the <a href="https://github.com/opencabstandard/opencab/blob/master/OpenCabConsumer/app/src/main/AndroidManifest.xml">AndroidManifest.xml</a>
 * in the OpenCabConsumer sample app.</p>
 *
 * <h3>Compliance</h3>
 * <p>This specification uses the phrases MUST, MUST NOT, MAY, SHOULD, and SHOULD NOT as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc2119">RFC 2119</a> to refer to functionality
 * that is required for compliance or that is optional, suggested, or recommended.</p>
 */
package org.opencabstandard.provider;
