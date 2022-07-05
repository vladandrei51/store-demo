Hello, welcome to my store management tool demo application

This is a demonstration of how a high level store management could work from the backend side.

The endpoints are built around the `Product`, are role-based acccessed and they offer basic functionalities like: 
- Get all or by ID - accesible to everyone
- Update product - accesible to `User`s and `Admin`s
- Delete product - accesible to `Admin`s
- Add new product - accesible to `User`s and `Admin`s

The `ProductController` was tested by both mocking `ProductControllerMockTests` and by integration testing `ProductControllerTests` (using controller's own implementation)
Both types of testings use either valid or invalid data to make use of the custom created `RecordNotFoundException` 

For simplicity sake it was used an in-memory db with some custom default data initialization in `data.sql`

The DB ER diagram is the following: 
![image](https://user-images.githubusercontent.com/26508725/177349349-7c34f29c-d76b-42c7-b5ed-b1b1a1abc3cc.png)
