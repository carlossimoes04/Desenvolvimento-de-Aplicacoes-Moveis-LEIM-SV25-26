# API Usage

## API Provider
**Picsum Photos** (https://picsum.photos)

## Endpoint
To retrieve a list of images, the application will use the following endpoint:
`GET https://picsum.photos/v2/list`

### Query Parameters (Optional)
* `page`: The page number to retrieve.
* `limit`: The number of items per page (default is 30).

## Response Format
The API returns a JSON array of objects. Each object contains metadata about a specific image.

## Example JSON Response
```json
[
    {
        "id": "0",
        "author": "Alejandro Escamilla",
        "width": 5000,
        "height": 3333,
        "url": "[https://unsplash.com/photos/yC-Yzbqy7PY](https://unsplash.com/photos/yC-Yzbqy7PY)",
        "download_url": "[https://picsum.photos/id/0/5000/3333](https://picsum.photos/id/0/5000/3333)"
    },
    {
        "id": "1",
        "author": "Alejandro Escamilla",
        "width": 5000,
        "height": 3333,
        "url": "[https://unsplash.com/photos/LNRyGwIJr5c](https://unsplash.com/photos/LNRyGwIJr5c)",
        "download_url": "[https://picsum.photos/id/1/5000/3333](https://picsum.photos/id/1/5000/3333)"
    }
]