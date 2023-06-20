var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();

var app = builder.Build();

// Configure the HTTP request pipeline.

app.UseAuthorization();

app.MapControllers(    
    name: "DefaultRoute",
    routeTemplate: "api/{controller}/{id}",
    defaults: new { id = System.Web.UI.WebControls.RouteParameter.Optional });

app.Run();
