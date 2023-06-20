using System.Collections.Generic;
using DataAccess;

var list1 = DataTable.New.ReadLazy(args[0]);

var list2 = DataTable.New.ReadLazy(args[1]);

var list3 = DataTable.New.FromEnumerableLazy(args);

List<Row> allItems = new List<Row>(list1.Rows);
allItems.AddRange(list2.Rows);

allItems.Sort();
foreach (var row in allItems)
{    
    Console.WriteLine("{0} {1} ({2})", row);
}

Console.ReadLine();
