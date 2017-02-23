package deepstreamHub;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.deepstream.*;

import java.net.URISyntaxException;

public class Runner {

    DeepstreamClient client;

    public Runner() {

        try {
            client = new DeepstreamClient("<Your app url here>");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client.login();

        // Records (data-sync)

        Record record = client.record.getRecord("test-record");

        // you can set the entire data
        JsonObject data = new JsonObject();
        data.addProperty("name", "Alex");
        data.addProperty("favouriteDrink", "coffee");
        record.set(data);

        // or just a path
        record.set( "hobbies", new String[]{ "sailing", "reading" });

        // and retrieved using .get()
        record.get(); // returns the entire data
        record.get( "hobbies[1]" ); // returns 'reading'

        //subscribe to changes made by you or other clients using .subscribe()
        record.subscribe(new RecordChangedCallback() {
            public void onRecordChanged(String recordName, JsonElement data) {
                // some value in the record has changed
            }
        });

        record.subscribe( "firstname", new RecordPathChangedCallback() {
            public void onRecordPathChanged(String recordName, String path, JsonElement data) {
                // the field "firstname" changed
            }
        });


        // Events (publish-subscribe)

        // Clients and backend processes can receive events using .subscribe()
        client.event.subscribe("test-event", new EventListener() {
            public void onEvent(String eventName, Object data) {
                // do something with data
            }
        });

        // and publish events using .emit()
        client.event.emit( "test-event", "some data");

        // RPCs (request-response)

        // You can make a request using .make()
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("a", 7);
        jsonObject.addProperty("b", 8);
        RpcResult rpcResult = client.rpc.make( "multiply-numbers", jsonObject);
        int result = (Integer) rpcResult.getData(); // 56

        // and answer it using .provide()
        client.rpc.provide("multiply-numbers", new RpcRequestedListener() {
            public void onRPCRequested(String name, Object data, RpcResponse response) {
                Gson gson = new Gson();
                JsonObject jsonData = (JsonObject) gson.toJsonTree(data);
                int a = jsonData.get("a").getAsInt();
                int b = jsonData.get("b").getAsInt();
                response.send(a * b);
            }
        });
    }


    public static void main(String[] args) {

        new Runner();

    }
}
