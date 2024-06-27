# XTR v3 
### REST request/response mappings for XRoad SOAP requests

### Services

Services are defined as YAML-formatted DSLs in folder specified in `application.dslPath` property.

The directory format should be `<DSLpath>/<service provider>/<service name>`.

```yaml
params:
  - <list of allowed parameters>
service: <uri of service>
method: <GET|POST>

envelope: <XRoad envelope as XML>
```
* `envelope` can contain handlebars mappings for parameters
* Only parameters specified in `params` will be applied for handlebars mappings

All services are served as POST endpoints.

    POST /<service provider>/<service_name>

with optiona parameters as JSON object in request body.