/**
 * This package contains the Java interface files that define the contracts for OpenCab
 * <i>providers</i>, the data types communicated to and from those providers, as well as abstract
 * implementations that reduce the boilerplate code needed for an implementation.
 *
 * <p>You can <a href="https://github.com/opencabstandard/opencab/edit/master/OpenCabProvider/app/src/main/java/org/opencabstandard/provider/package-info.java">propose changes or improvements to this documentation using GitHub.</a>
 *
 * <h2>1. Overview</h2>
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
 * <h3>2. Package visibility</h3>
 * <p><b>Note: Provider declaration has been updated from previous versions of the specification to
 * support multiple provider apps installed at once.</b> See Section 6.2 "Compatibility with providers
 * which predate Android enforcement of unique authority names" for details about how to
 * support providers using the older approach.</p>
 *
 * <p>In order to allow provider and consumer apps to communicate with each other on <a
 * href="https://developer.android.com/training/package-visibility">modern Android versions</a>,
 * participating apps must include certain declarations in <code>AndroidManifest.xml</code>. The
 * specific declarations needed for providers and consumers are discussed in detail below.</p>
 *
 * <h3>3. Providers</h3>
 *
 * <h4>3.1. Base requirements</h4>
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
 * <h4>3.2. Manifest requirements for each implemented contract</h4>
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
 * <h4>3.3. Manifest requirements for broadcast intents</h4>
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
 * <h4>3.4. Publishing broadcast intents</h4>
 * <p>When publishing a broadcast intent that is defined as part of a contract, all providers:</p>
 * <ol>
 * <li>MUST enumerate each visible package on the device. For each visible package, the provider
 * MUST enumerate each receiver available on the PackageInfo receivers property and check if the
 * receiver name ends with a period (“.”) followed by the receiver class name. For example, if package
 * <code>com.example</code> defines a receiver called <code>com.example.IdentityChangedReceiver</code> and package
 * <code>net.subtle.foo.bar.IdentityChangedReceiver</code>, a provider implementing the identity contract and
 * broadcasting the <code>ACTION_IDENTITY_INFORMATION_CHANGED</code> event would send intents to both receiver classes.</li>
 * <li>MUST publish events for each discovered receiver class by calling <code>sendBroadcast</code> with an explicit {@link android.content.Intent}
 * with its component <code>property</code> set to the package and fully qualified class
 * name of the enumerated receiver class. This ensures that apps which have been terminated, stopped, or updated
 * since last launch will be started by the OS and given an opportunity to handle the event.</li>
 * </ol>
 *
 * <h4>3.5. Backwards- and forwards-compatibility</h4>
 * <p>To allow the specification to evolve without upgrading all participating apps in lockstep, both providers and consumers
 * need to implement certain behavior to ensure compatibility when interacting with both older and newer apps.</p
 * <p>Providers MUST examine the KEY_VERSION property of calls they receive and apply the following logic:</p>
 * <ol>
 *   <li>If the consumer-requested version is lower than (see Versioning below) any supported contract version, the provider MUST return a value for KEY_ERROR.
 *     <p>For example, if a consumer requests version 0.2 but the provider has only chosen to implement version 0.3 of the contract, the provider would populate KEY_ERROR with an explanatory message.</p></li>
 *   <li>If the consumer-requested version is one of the contract versions supported by the provider, the provider MUST return only the top-level Bundle keys defined in that version and MUST only return a response composed of the Parcelable data classes defined in that contract version. The provider MUST populate KEY_VERSION with the version of the contract matching the request. Providers MAY support an arbitrary number of older contract versions if desired.</p>
 *     <p>For example, if a consumer requests version 0.2 from a provider that supports both 0.3 and 0.2, the provider would return a response corresponding to version 0.2 of the contract, and it would set the KEY_VERSION property to be 0.2 to indicate it understood the older request and that the consumer should parse the response according to version 0.2 of the contract. It would be an error for the provider to return Bundle keys or parceled objects from newer versions of the contract than requested, as these are likely to cause errors when the consumer processes the response.</p></li>
 *   <li>If the consumer-requested version is higher than (see Versioning below) the newest supported contract version, the provider MUST behave as if the latest version it supports was requested instead. This includes setting the KEY_VERSION to match the returned data so that the newer consumer can determine that the provider only supports an older contract version and modify its behavior accordingly.
 *     <p>For example, if a provider that supports version 0.2 and version 0.3 of a contract receives a request for version 0.5, the provider would return a response corresponding to version 0.3 of the contract, and it would set the KEY_VERSION property to be 0.3 to indicate it did not understand the request for a newer version and that the consumer SHOULD parse the response according to version 0.3 of the contract, or MAY treat the response as an error if the consumer does not wish to implement backwards compatibility.</p></li>
 *   <li>Versioning was formally introduced with version 0.3 of each contract. If the version parameter of a request made by a consumer is absent, the provider MUST treat the request as if the version were set to “0.2”.</li>
 * </ol>
 *
 * <h3>4. Consumers</h3>
 *
 * <h4>4.1. Base requirements</h4>
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
 * <h4>4.2. Manifest requirements for broadcast intents</h4>
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
 * <h4>4.3. Selecting and calling providers</h4>
 * <p>To use or display the information available through a contract, a consumer must first select one or more providers which implement a supported version of that contract. When selecting a provider:</p>
 * <ol>
 *   <li>Consumers MUST enumerate each visible package on the device. For each visible package, the provider MUST enumerate each provider available on the PackageInfo providers property and check if the provider authority ends with a period (“.”) followed by the fully-qualified contract name.
 *     <p>For example, if package com.example defines a provider called com.example.org.opencabstandard.hos and package net.subtle.foo.bar.org.opencabstandard.hos, a consumer wishing to use or display HOS information MAY consider either or both applications as a source of data.</p>
 *     <p>However, a package biz.nobodyorg defining a provider called biz.nobodyorg.opencabstandard.hos would not be considered as a potential source of information because no period (“.”) is present before the fully-qualified contract name.</p>
 *   </li>
 *   <li>Consumers MAY use the contract version set in KEY_VERSION (as distinct from the requested version) after making one or more calls to a provider as a factor in determining whether to continue to use the provider or whether to select an alternative provider instead.
 *     <p>For example, if a consumer requests version 0.3 identity information but receives version 0.2 contract information in response, the consumer might choose to use another identity provider installed on the same device instead.</p></li>
 * </ol>
 *
 * <h4>4.4. Backwards- and forwards-compatibility</h4>
 * <ol>
 *   <li>Consumers MUST set the version parameter of each call to match the latest version of the contract they support.</li>
 *   <li>Consumers MUST examine the KEY_VERSION present in the response and treat the response as an error if the version is higher than any version the consumer supports. For example, if a consumer requests version 0.3 but the provider is not compliant with section 3.5 and returns a 0.4 response, the consumer MUST detect this and treat it as an error.</li>
 *   <li>Consumers SHOULD support multiple response versions for backwards compatibility with older providers by examining the returned KEY_VERSION, keeping in mind that it may be lower than the requested version. For example, a provider might request version 0.3, but an older provider implementing the logic from section 3.5 might return a 0.2 response because that is the highest version of the contract it supports. The consumer MAY handle this response according to the version 0.2 definition, or it MAY treat the response as an error if backwards compatibility is not desired.</li>
 *   <li>Versioning was formally introduced with version 0.3 of each contract. If KEY_VERSION is absent from a provider’s response, the consumer MUST treat the response as if the version were set to “0.2”.</li>
 * </ol>
 *
 * <h3>5. Security</h3>
 * <p>At this time, limiting the exchange of data with another OpenCab-enabled app for reasons of
 * privacy or security is left up to individual implementations. Providers and consumers MAY use
 * OS-level mechanisms (such as <a href="https://developer.android.com/reference/android/content/ContentProvider#getCallingPackage()">ContentProvider.getCallingPackage()</a>)
 * to limit the exchange of data to packages that have been specifically permitted by an end user or
 * customer.</p>
 *
 * <h3>6. Version compatibility</h3>
 *
 * <h4>6.1. Version comparison</h4>
 * <p>For correct behavior, OpenCab version strings must be compared logically, not lexically or numerically.</p>
 * <p>The comparison algorithm is identical to that defined in <a href="https://semver.org/spec/v2.0.0.html#spec-item-11">Semantic Versioning 2.0.0, Item 11.</a></p>
 *
 * <h4>6.2. Compatibility with providers which predate Android enforcement of unique authority names</h4>
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
 * <h3>7. Compliance</h3>
 * <p>This specification uses the phrases MUST, MUST NOT, MAY, SHOULD, and SHOULD NOT as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc2119">RFC 2119</a> to refer to functionality
 * that is required for compliance or that is optional, suggested, or recommended.</p>
 */
package org.opencabstandard.provider;
