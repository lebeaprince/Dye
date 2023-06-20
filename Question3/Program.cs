using Google.Apis.Auth.OAuth2;
using Google.Apis.Gmail.v1;
using Google.Apis.Gmail.v1.Data;
using Google.Apis.Services;
using Google.Apis.Util.Store;

class Program
{
    static string[] Scopes = { GmailService.Scope.GmailReadonly };
    static string ApplicationName = "Test App";

    static void Main(string[] args)
    {
        UserCredential credential;

        using (var stream =
            new FileStream("credentials.json", FileMode.Open, FileAccess.Read))
        {
            string credPath = "token.json";
            credential = GoogleWebAuthorizationBroker.AuthorizeAsync(
                GoogleClientSecrets.FromStream(stream).Secrets,
                Scopes,
                "user",
                CancellationToken.None,
                new FileDataStore(credPath, true)).Result;
            Console.WriteLine("Credential file saved to: " + credPath);
        }

        var service = new GmailService(new BaseClientService.Initializer()
        {
            HttpClientInitializer = credential,
            ApplicationName = ApplicationName,
        });

        UsersResource.MessagesResource.ListRequest request = service.Users.Messages.List("me");

        ListMessagesResponse response = request.Execute();

        foreach (var email in response.Messages.Take(10))
        {
            var emailInfoReq = service.Users.Messages.Get("me", email.Id);
            var emailInfoResponse = emailInfoReq.Execute();

            if (emailInfoResponse != null)
            {
                var subject = emailInfoResponse.Payload.Headers.FirstOrDefault(x => x.Name == "Subject");

                if (subject != null)
                {
                    Console.WriteLine(subject.Value);
                }
            }
        }

        Console.Read();
    }
}