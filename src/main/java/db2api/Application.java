package db2api;

import db2api.checker.BaseAccessChecker;
import db2api.infra.Pipeline;
import db2api.model.ApiModel;
import db2api.model.ConfigModel;
import db2api.model.HttpContext;
import db2api.model.SqlStatements;
import db2api.parser.api.BaseJsonApiParser;
import db2api.parser.config.BaseJsonConfigParser;
import db2api.translator.BaseSqlTranslator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Application {
  public static void main(String[] args) {
    if (args.length != 2) {
      // System.out.println("Please give an config JSON file and an API request JSON
      // file.");
      args = new String[] { "config.json", "request.json" };
    }

    String configText = "";
    try {
      configText = Files.readString(Paths.get(args[0]));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String requestText = "";
    try {
      requestText = Files.readString(Paths.get(args[1]));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    final String DATA_API = "api";
    final String DATA_CONFIG = "config";
    final String DATA_SQL = "sql";

    var pipeline = new Pipeline();
    pipeline.data.push(DATA_CONFIG, new BaseJsonConfigParser().parseText(configText));

    pipeline.middlewares.add((context, data)->{
      data.push(DATA_API, new BaseJsonApiParser().parseText(context.request.body));
      return false;
    });
    pipeline.middlewares.add((context, data)->{
      var result = new BaseAccessChecker().check(data.<ApiModel>get(DATA_API), data.<ConfigModel>get(DATA_CONFIG));
      if (result.size() > 0) {
        context.response.status = 401;
        context.response.message = String.join("\n", result);
        return true;
      }
      return false;
    });
    pipeline.middlewares.add((context, data)->{
      data.push("DATA_SQL", new BaseSqlTranslator().translate(data.<ApiModel>get(DATA_API), data.<ConfigModel>get(DATA_CONFIG)));
      return false;
    });
    pipeline.handler = (context, data)->{
      var result = data.<SqlStatements>get(DATA_SQL);
      System.out.println("Generated SQL:");
      for (String sql : result.getStatements()) {
        System.out.println(sql);
        executeSQL(sql);
      }
    };

    var request = new HttpContext.Request();
    request.body = requestText;

    var response = pipeline.resolve(request);

    System.out.println(response.ToJSON());


//    new Solver().solve(requestText, configText);
  }

  static void executeSQL(String sql) {
    final String targetUrl = "";
    var client = HttpClient.newHttpClient();
    var sqlr = new SQLRequest();
    sqlr.setSql(sql);

    if (sql.startsWith("select")) {
      sqlr.setType("query");
    } else {
      sqlr.setType("execute");
    }

    var request = HttpRequest.newBuilder().uri(
                    URI.create(targetUrl))
            .header("Content-Type", "application/json")
            .POST(
                    HttpRequest.BodyPublishers.ofByteArray(
                            Utils.toJSON(sqlr).getBytes()))
            .build();
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println(response.body());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

class Solver {
  ApiModel parseApi(String text) {
    return new BaseJsonApiParser().parseText(text);
  }

  ConfigModel parseConfig(String text) {
    return new BaseJsonConfigParser().parseText(text);
  }

  ArrayList<String> check(ApiModel request, ConfigModel config) {
    return new BaseAccessChecker().check(request, config);
  }

  SqlStatements translate(ApiModel request, ConfigModel config) {
    return new BaseSqlTranslator().translate(request, config);
  }

  public void solve(String requestText, String configText) {
    ApiModel request = parseApi(requestText);

    System.out.println("Parsed API request:");
    System.out.println(request.ToJSON());

    ConfigModel config = parseConfig(configText);

    System.out.println("Parsed Config request:");
    System.out.println(config.ToJSON());

    ArrayList<String> checked = check(request, config);
    if (checked.size() != 0) {
      throw new RuntimeException("Failed to pass checking:\n" + String.join("\n", checked));
    }

    SqlStatements result = translate(request, config);
    System.out.println("Generated SQL:");
    for (String sql : result.getStatements()) {
      System.out.println(sql);
      executeSQL(sql);
    }
  }

  void executeSQL(String sql) {
    final String targetUrl = "";
    var client = HttpClient.newHttpClient();
    var sqlr = new SQLRequest();
    sqlr.setSql(sql);

    if (sql.startsWith("select")) {
      sqlr.setType("query");
    } else {
      sqlr.setType("execute");
    }

    var request = HttpRequest.newBuilder().uri(
        URI.create(targetUrl))
        .header("Content-Type", "application/json")
        .POST(
            HttpRequest.BodyPublishers.ofByteArray(
                Utils.toJSON(sqlr).getBytes()))
        .build();
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println(response.body());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

class SQLRequest {
  String type;

  String sql;

  public String getType() {
    return type;
  }

  public void setType(String val) {
    type = val;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String val) {
    sql = val;
  }
}