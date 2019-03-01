package ch.rethab.cg;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CrushStorage {

    private final RequestQueue queue;

    CrushStorage(RequestQueue queue) {
        this.queue = queue;
    }

    private static String url = "https://kvdb.io/5bxxHsUSPgMv2VHsqhUB82/";

    public void registerPlayer(String playerName, final Response.Listener<Player> listener) {
        final Player p = new Player(playerName, 0);
        queue.add(
                new PostableJsonObjectRequest(
                        url+playerName,
                        p.toJson(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                listener.onResponse(p);
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) { error.printStackTrace(); }
                        }
                )
        );
    }

    void getPlayer(final String playerName, final Response.Listener<Player> listener) {
        queue.add(
                new JsonObjectRequest(
                        Request.Method.GET,
                        url + playerName,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("CrushStorage", "getPlayer("+playerName+") => " + response.toString());
                                listener.onResponse(Player.fromJson(response));
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                if(error.networkResponse.statusCode == 404) {
                                    listener.onResponse(null);
                                }
                            }
                        }
                )
        );
    }

    void incCrushes(final String playerName, final Response.Listener<Player> listener) {
        Log.d("CrushStorage", "incCrushes("+playerName+")");
        getPlayer(playerName, new Response.Listener<Player>() {
            @Override
            public void onResponse(Player p) {
                final Player updated = p.incCrushes();

                queue.add(
                        new PostableJsonObjectRequest(
                                url+playerName,
                                updated.toJson(),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("CrushStorage", "incCrushes("+playerName+") ==> " + response.toString());
                                        listener.onResponse(updated);
                                    }
                                },
                                new Response.ErrorListener(){
                                    @Override
                                    public void onErrorResponse(VolleyError error) { error.printStackTrace(); }
                                }
                        )
                );
            }
        });
    }

}

class Player {
    private final String name;
    final int crushes;

    Player(String name, int crushes) {
        this.name = name;
        this.crushes = crushes;
    }

    Player incCrushes() {
        return new Player(name, crushes + 1);
    }

    JSONObject toJson() {
        JSONObject product = new JSONObject();
        try {
            product.put("name", name);
            product.put("crushes", crushes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return product;
    }

    static Player fromJson(JSONObject json) {
        try {
            return new Player(
                    json.getString("name"),
                    json.getInt("crushes")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}

class PostableJsonObjectRequest extends JsonObjectRequest {

    PostableJsonObjectRequest  (String url, JSONObject payload, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, url, payload, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            // the json parsing otherwise fails with an empty string
            if (response.data.length == 0) {
                byte[] responseData = "{}".getBytes("UTF8");
                return super.parseNetworkResponse(new NetworkResponse(responseData));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return super.parseNetworkResponse(response);
    }
}
