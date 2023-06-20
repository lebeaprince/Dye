using ClosedXML.Excel;
using Microsoft.AspNetCore.Mvc;
using Question2.Models;
using System.Collections;
using System.Linq;

namespace Question2.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class RecipesController : ControllerBase
    {
        [HttpGet]
        public IEnumerable<Recipe> Get()
        {
            return GetRecipes();
        }

        [HttpGet("export")]
        public IActionResult Export()
        {
            var recipes = GetRecipes();

            using (var workbook = new XLWorkbook())
            {
                foreach (var recipe in recipes)
                {
                    var worksheet = workbook.Worksheets.Add(recipe.Name);
                    worksheet.Cell(1, 1).Value = "Id";
                    worksheet.Cell(1, 2).Value = "Ingredient";
                    worksheet.Cell(1, 3).Value = "Quantity";

                    var row = 2;
                    foreach (var ingredient in recipe.Ingredients)
                    {
                        worksheet.Cell(row, 1).Value = ingredient.Id;
                        worksheet.Cell(row, 2).Value = ingredient.Name;
                        worksheet.Cell(row, 3).Value = ingredient.Quantity;
                        row++;
                    }
                }

                //Code to release resources to prevent memory leak
                byte[] content = null;
                try{
                        using(MemoryStream outputStream = new MemoryStream()){
                            workbook.SaveAs(outputStream);
                            content = outputStream.ToArray();
                        }
                }catch(OutOfMemoryException ex){
                    Console.WriteLine(ex.Message);
                }



                return File(
                    content,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "recipes.xlsx");
            }
        }

        private IEnumerable<Recipe> GetRecipes(string filter = null)
        {
            var recipes = new List<Recipe>()
            {
                new Recipe()
                {
                    Id = 1,
                    Name = "Spaghetti Bolognese",
                    Ingredients = new List<Ingredient>()
                    {
                        new Ingredient() { Id = 1, Name = "Spaghetti", Quantity = "200g" },
                        new Ingredient() { Id = 2, Name = "Minced Meat", Quantity = "500g" },
                        new Ingredient() { Id = 3, Name = "Tomato Sauce", Quantity = "300g" },
                    }
                },
                new Recipe()
                {
                    Id = 2,
                    Name = "Chicken Curry",
                    Ingredients = new List<Ingredient>()
                    {
                        new Ingredient() { Id = 4, Name = "Chicken Breast", Quantity = "500g" },
                        new Ingredient() { Id = 5, Name = "Curry Powder", Quantity = "2 tbsp" },
                        new Ingredient() { Id = 6, Name = "Coconut Milk", Quantity = "400g" },
                    }
                },
                new Recipe()
                {
                    Id = 3,
                    Name = "Beef Stew",
                    Ingredients = new List<Ingredient>()
                    {
                        new Ingredient() { Id = 7, Name = "Beef", Quantity = "500g" },
                        new Ingredient() { Id = 8, Name = "Carrots", Quantity = "2" },
                        new Ingredient() { Id = 9, Name = "Potatoes", Quantity = "3" },
                    }
                }
            };
            if(!string.IsNullOrEmpty(filter)){
             return recipes.where(r => r.Name.Contains(filter));               
            }
            return recipes;
        }
    }
}