import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

import java.util.Base64;

public class SecretApp {
    private static void help() {
        System.out.println("Retrieves a secret from AWS secrets manager.");
        System.out.println("Syntax:");
        System.out.println("secretapp region secretname");
    }



    public static void main(String[] args) {
        if (args.length != 2) {
            help();
            return;
        }

        String region = args[0];
        String secretName = args[1];

        try {
            String secret = getSecret(secretName, region);
            System.out.println(secret);
        }
        catch(Exception e) {
            System.out.println("Something went wrong. Exception: ");
            System.out.println(e.getMessage());
        }
    }

    public static String getSecret(String secretName, String region) {
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();

        // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
        // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);

        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if (getSecretValueResult.getSecretString() != null) {
            return getSecretValueResult.getSecretString();
        }
        else {
            return new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
        }
    }
}
