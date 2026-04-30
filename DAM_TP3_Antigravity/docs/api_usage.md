# API Usage

## API Provider
**Picsum Photos** (https://picsum.photos)

## Endpoint
`GET https://picsum.photos/v2/list`

### Query Parameters
* `page`: Page number.
* `limit`: Items per page.

## Data Handling (in `:core`)
The API service is implemented using **Retrofit**. The responses are parsed into `ImageItem` objects using **Gson** or **Kotlinx.Serialization**.
