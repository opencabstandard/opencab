# OpenCab

A standard for communication between in-cab trucking apps.

For high-level information about OpenCab, see <https://opencabstandard.org>.

For the latest stable interface docs covering the currently-defined providers and data, see <https://docs.opencabstandard.org/>.

## In this repository

See [org/opencabstandard/provider/](OpenCabProvider/app/src/main/java/org/opencabstandard/provider/) for the actual interface files defining the key constants used for communicating information between apps.

`HOSContract` and `IdentityContract` are boilerplate classes you can use if you prefer not to define your own string constants, and `AbstractHOSProvider` and `AbstractIdentityProvider` are abstract classes you can subclass to make provider implementation even easier. Both are provided as-is and you aren't required to use them—you can absolutely implement the provider interface by subclassing `ContentProvider` directly on your own.

See [OpenCabProvider/](OpenCabProvider) for an example of a collaborating application that provides identity and HOS information to a consuming workflow application.
