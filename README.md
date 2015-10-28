# Reflektor
Abstraction around reflection to allow complete functionality of reflected classes


###Example

```
 //Say you want to use a Geofencing event, without importing it


//create the reflektor object using the fromIntent method instead of a normal constructor
final Reflektor geofencingEvent = new Reflektor("com.google.android.gms.location.GeofencingEvent", "fromIntent", new Object[]{intent});

//now you have the object call whatever method you want on it.
//say I want to see if it has an error code
boolean hasError = (boolean) geofencingEvent.invoke("hasError");

//if it does have an error I want to see what the error code is
if (hasError) {
       int errorCode = (int) geofencingEvent.invoke("getErrorCode");
}


//Using googleAPIClient.Builder

//create an instance using a constructor which takes in context
Reflektor apiClientBuilder = new Reflektor("com.google.android.gms.common.api.GoogleApiClient.Builder", new Object[]{context});

//add an API using the addApi method which has a parameter of the API
apiClientBuilder.invoke("addApi", new Object[]{api});
            
//add a listener
            apiClientBuilder.invoke("addConnectionFailedListener", new Object[]{listener});
            
//build the client (will return that actual GoogleApiClient)
Object apiClient = apiClientBuilder.invoke("build");
            
            
//if you ever want to get the target class
Class builderClass = apiClientBuilder.getTargetClass();
            
//if you want to get the actual builder itself
Object builder = apiClientBuilder.getTarget();

            
```
