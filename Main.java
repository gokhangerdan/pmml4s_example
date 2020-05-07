 
import static spark.Spark.*;
import com.google.gson.Gson;
import org.pmml4s.model.Model;

public class Main {

    public static void main(String[] args) {

        ModelStream ms = new ModelStream();

        Model model = Model.fromInputStream(ms.in);

        String[] inputNames = model.inputNames();

        post("/predict", (request, response) -> {
            response.type("application/json");
            DocumentString document = new Gson().fromJson(request.body(), DocumentString.class);

            String[] words = document.document.replaceAll("[^a-zA-ZığĞüÜşŞİöÖçÇ ]", "").toLowerCase().split("\\s+");

            int[] arr = new int[inputNames.length];

            for (int i = 0; i < inputNames.length; i++) {
                arr[i] = 0;
                for (int j = 0; j < words.length; j++) {
                    if (inputNames[i].equals(words[j])) {
                        arr[i] = 1;
                        break;
                    }
                }
            }

            DocumentString result = new DocumentString();

            result.document = model.predict(arr)[0].toString();

            return new Gson()
                    .toJson(result);
        });
    }
}
