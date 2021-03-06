package monero.rpc;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import common.types.HttpException;
import common.utils.JsonUtils;
import common.utils.StreamUtils;
import monero.utils.MoneroUtils;
import monero.wallet.model.MoneroException;

/**
 * Sends requests to the Monero RPC API.
 */
public class MoneroRpc {

  // logger
  private static final Logger LOGGER = Logger.getLogger(MoneroRpc.class);

  // custom mapper to deserialize integers to BigIntegers
  public static ObjectMapper MAPPER;
  static {
    MAPPER = new ObjectMapper();
    MAPPER.setSerializationInclusion(Include.NON_NULL);
    MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    MAPPER.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
  }

  // instance variables
  private String rpcHost;
  private int rpcPort;
  private URI rpcUri;
  private HttpClient client;

  public MoneroRpc(String endpoint) {
    this(MoneroUtils.parseUri(endpoint));
  }

  public MoneroRpc(URI rpcUri) {
    this.rpcUri = rpcUri;
    this.rpcHost = rpcUri.getHost();
    this.rpcPort = rpcUri.getPort();
    this.client = HttpClients.createDefault();
  }

  public MoneroRpc(String rpcHost, int rpcPort) throws URISyntaxException {
    this(rpcHost, rpcPort, null, null);
  }

  public MoneroRpc(String rpcHost, int rpcPort, String username, String password) throws URISyntaxException {
    this.rpcHost = rpcHost;
    this.rpcPort = rpcPort;
    this.rpcUri = new URI("http", null, rpcHost, rpcPort, "/json_rpc", null, null);
    if (username != null || password != null) {
      CredentialsProvider creds = new BasicCredentialsProvider();
      creds.setCredentials(new AuthScope(rpcUri.getHost(), rpcUri.getPort()), new UsernamePasswordCredentials(username, password));
      this.client = HttpClients.custom().setDefaultCredentialsProvider(creds).build();
    } else {
      this.client = HttpClients.createDefault();
    }
  }

  public String getRpcHost() {
    return rpcHost;
  }

  public int getRpcPort() {
    return rpcPort;
  }

  public URI getRpcUri() {
    return rpcUri;
  }
  
  /**
   * Sends a request to the RPC API.
   * 
   * @param method specifies the method to request
   * @return Map<String, Object> is the RPC API response as a map
   */
  public Map<String, Object> sendRpcRequest(String method) {
    return sendRpcRequest(method, (Map<String, Object>) null);
  }
  
  /**
   * Sends a request to the RPC API.
   * 
   * @param method specifies the method to request
   * @param params specifies input parameters (Map<String, Object>, List<Object>, String, etc)
   * @return Map<String, Object> is the RPC API response as a map
   */
  public Map<String, Object> sendRpcRequest(String method, Object params) {

    // send http request
    try {

      // build request body
      Map<String, Object> body = new HashMap<String, Object>();
      body.put("jsonrpc", "2.0");
      body.put("id", "0");
      body.put("method", method);
      if (params != null) body.put("params", params);
      LOGGER.debug("Sending method '" + method + "' with body: " + JsonUtils.serialize(body));

      // send http request and validate response
      HttpPost post = new HttpPost(rpcUri);
      HttpEntity entity = new StringEntity(JsonUtils.serialize(body));
      post.setEntity(entity);
      HttpResponse resp = client.execute(post);
      validateHttpResponse(resp);

      // deserialize response
      Map<String, Object> respMap = JsonUtils.toMap(MAPPER, StreamUtils.streamToString(resp.getEntity().getContent()));
      LOGGER.debug("Received response to method '" + method + "': " + JsonUtils.serialize(respMap));
      EntityUtils.consume(resp.getEntity());

      // check RPC response for errors
      validateRpcResponse(respMap, body);
      return respMap;
    } catch (HttpException e1) {
      throw e1;
    } catch (MoneroRpcException e2) {
      throw e2;
    } catch (Exception e3) {
      e3.printStackTrace();
      throw new MoneroException(e3);
    }
  }
  
  // ------------------------------ STATIC UTILITIES --------------------------

  private static void validateHttpResponse(HttpResponse resp) {
    int code = resp.getStatusLine().getStatusCode();
    if (code < 200 || code > 299) {
      String content = null;
      try {
        content = StreamUtils.streamToString(resp.getEntity().getContent());
      } catch (Exception e) {
        // could not get content
      }
      throw new HttpException(code, resp.getStatusLine().getReasonPhrase() + (content != null ? (": " + content) : ""));
    }
  }

  @SuppressWarnings("unchecked")
  private static void validateRpcResponse(Map<String, Object> respMap, Map<String, Object> requestBody) {
    Map<String, Object> error = (Map<String, Object>) respMap.get("error");
    if (error == null) return;
    int code = ((BigInteger) error.get("code")).intValue();
    String message = (String) error.get("message");
    throw new MoneroRpcException(code, message, requestBody);
  }
}
