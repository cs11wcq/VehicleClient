package mitchell;

import mitchell.client.VehicleClient;
import mitchell.model.Vehicle;
import org.springframework.web.client.HttpClientErrorException;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;

/**
 * Mr L is testing
 */
public class TestVehicleClient
{
    public static void main(String[] args)
    {
        VehicleClient client = new VehicleClient();

        Vehicle vYearOutOfBounds = new Vehicle(1900, "Cool", "Wagon");
        Vehicle v1 = new Vehicle(1999, "Ford", "Focus");
        Vehicle v2 = new Vehicle(2020, "Ford", "Mustang");
        Vehicle v3 = new Vehicle(2020, "Toyota", "Prius");

        Vehicle vUpdated = new Vehicle(2021, "Lightning", "Bolt");
        try
        {
            client.createVehicle(vYearOutOfBounds); //this generates an error which is good since year is out of bounds
        }
        catch(HttpClientErrorException e)
        {
            System.out.println("\nYEAR OUT OF BOUNDS ERROR MSG" + e.getMessage());
        }

    //CREATE VEHICLE
        System.out.println("\nCREATE");
        Vehicle v1Output = client.createVehicle(v1);
        Vehicle v2Output = client.createVehicle(v2);
        Vehicle v3Output = client.createVehicle(v3);

        //test get vehicle with a particular ID
        System.out.println("\t\tTest GET BY ID");
        client.getVehicleByID(v1Output.getId()).printVehicleContents();

    //DELETE BY ID
        System.out.println("\nDELETE");
        List<Vehicle> allBefore = client.getAllVehicles();
        System.out.println("\nNUMBER OF ELEMENTS IN DATABASE: " + allBefore.size() + "\n");
        client.deleteVehicleById(v1Output.getId()); //delete vehicle
        //test an exception was thrown- its really the ResourceNotFoundException
        //ex: "status":404,"error":"Not Found","message":"Vehicle not found with id: 8"
        try
        {
            client.deleteVehicleById(-1); //delete vehicle

        }
        catch(HttpClientErrorException e)
        {
            System.out.println("\nDELETE INVALID ID ERROR MSG" + e.getMessage() + "\n");
        }
        List<Vehicle> allAfter = client.getAllVehicles();
        System.out.println("\nNUMBER OF ELEMENTS IN DATABASE AFTER DELETING: " + allAfter.size() + "\n");
//        client.getVehicleByID(v1Output.getId()); //should generate error since that ID was not found

    //UPDATE BY ID
        System.out.println("\nUPDATE");
        Vehicle originalVehicleFromDataBase = client.getVehicleByID(2);
        System.out.println("\nBEFORE UPDATING\n");
        originalVehicleFromDataBase.printVehicleContents();
        Vehicle updatedVehicleFromDataBase = client.updateVehicleById(2, vUpdated);
        System.out.println("\nAFTER UPDATING\n");
        updatedVehicleFromDataBase.printVehicleContents();

        //try updating a vehicle with an ID that does not exist. should generate error msg
        try
        {
            client.updateVehicleById(100, vUpdated);
        }
        catch (HttpClientErrorException e)
        {
            System.out.println("\nUPDATE INVALID ID ERROR MSG" + e.getMessage() + "\n");
        }

    //GET ALL vehicles
        List<Vehicle> list0 = client.getAllVehicles();
        for (Vehicle v: list0)
        {
            v.printVehicleContents();
        }

        //test1- should print out a list of vehicles with year 1999, make Ford model Focus
        List<Vehicle> list1 = client.getVehiclesByYearAndMake("1999", "Ford");
        for (Vehicle v: list1)
        {
            v.printVehicleContents();
        }

        //test getting all vehicles by year
        List<Vehicle> list2 = client.getVehiclesByYear("1999");
        for (Vehicle v: list1)
        {
            v.printVehicleContents();
        }

        //test getting all vehicles by model mustang
        List<Vehicle> list3 = client.getVehiclesByModel("Mustang");
        for (Vehicle v: list3)
        {
            v.printVehicleContents();
        }

        List<Vehicle> list5 = client.getVehiclesByYearAndMakeAndModel("2020", "Toyota", "Prius");
        for (Vehicle v: list5)
        {
            v.printVehicleContents();
        }

        //test- there are no vehicles like this, so should not print out anything
        List<Vehicle> list = client.getVehiclesByYearAndMakeAndModel("1950", "toyota", "prius");
        for (Vehicle v: list)
        {
            v.printVehicleContents();
        }


    //TEST clearing out database
        List<Vehicle> dataListBefore = client.getAllVehicles();
        System.out.println("\nNUMBER OF ELEMENTS IN DATABASE BEFORE CLEARING: " + dataListBefore.size() + "\n");
        client.cleanDataBase();
        List<Vehicle>dataListAfter = client.getAllVehicles();
        System.out.println("\nNUMBER OF ELEMENTS IN DATABASE AFTER CLEARING: " + dataListAfter.size() + "\n");
        // now the database is empty

    //UPDATE LIST OF VEHICLES.
        System.out.println("\nUPDATE LIST OF VEHICLES**************************************");
        Vehicle vehicle0 = new Vehicle(2020, "toyota", "honda");
        Vehicle vehicle1 = new Vehicle(2020, "lightning", "bolt");

        //post the vehicles to the database and
        // remember that createVehicle() returns that vehicle from the database, and also gives it an ID
        Vehicle vehicle0FromDataBase = client.createVehicle(vehicle0);
        Vehicle vehicle1FromDataBase = client.createVehicle(vehicle1);
        //add these vehicles to a list
        List<Vehicle> originalVehicleListFromDataBase= new ArrayList<>();
        originalVehicleListFromDataBase.add(vehicle0FromDataBase);
        originalVehicleListFromDataBase.add(vehicle1FromDataBase);


        System.out.println("\nLIST OF VEHICLES IN DATABASE BEFORE UPDATING" + "\n");
        //and print out the list of vehicles from the database before updating
        for (Vehicle v: originalVehicleListFromDataBase)
        {
            v.printVehicleContents();
        }

        //set up the updated vehicles that will replace the original vehicles in the database
        Vehicle vehicle0Updated = new Vehicle(2000, "toyota", "prius");
        //make sure it has the same id otherwise it won't be able to locate vehicle0 to replace it
        vehicle0Updated.setId(vehicle0FromDataBase.getId());
        Vehicle vehicle1Updated = new Vehicle(2010, "USAIN", "bolt");
        vehicle1Updated.setId(vehicle1FromDataBase.getId());

        //make a list of all the updated vehicles
        List<Vehicle> updatedList = new ArrayList<>();
        updatedList.add(vehicle0Updated);
        updatedList.add(vehicle1Updated);

        List<Vehicle> updatedVehicleListFromDataBase= new ArrayList<>();
        //updateVehicles() from VehicleClient will take updatedList (the list of updated vehicles)
        //and update all of the corresponding vehicles in the database, and return the list of updated vehicles
        //from the database and store that in updatedVehicleListFromDataBase
        updatedVehicleListFromDataBase = client.updateVehicles(updatedList);

        System.out.println("\nLIST OF VEHICLES IN DATABASE * AFTER * UPDATING" + "\n");
        //print out the updated list of vehicles from the database
        for (Vehicle v: updatedVehicleListFromDataBase)
        {
            v.printVehicleContents();
        }
    }
}
