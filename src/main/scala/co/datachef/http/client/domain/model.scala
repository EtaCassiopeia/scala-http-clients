package co.datachef.http.client.domain

//https://documentation.expoints.nl/external/Help/ResourceModel?modelName=Expoints.API.Extern.V2.Models.Customer
case class Customer(Id: Int, AccessKey: String /* rest of the fields */ )

case class CustomerResponse(Customers: List[Customer], NextCursor: String)
