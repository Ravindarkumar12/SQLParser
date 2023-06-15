package test.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.parser.CCJSqlParserDefaultVisitor;
import net.sf.jsqlparser.parser.CCJSqlParserTreeConstants;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import net.sf.jsqlparser.util.AddAliasesVisitor;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws JSQLParserException {
		String sqlString = "SELECT OrderID, Quantity,(CASE WHEN Quantity > 30 THEN 'The quantity is greater than 30' WHEN Quantity = 30 THEN 'The quantity is 30' ELSE 'The quantity is under 30' END) AS QuantityText FROM OrderDetails;";
		//parseSQL(sqlString);
		//parseSQLComplex();
		parseSQL1(sqlString);
		//parseSQLWithColumns(sqlString);
		//parseSQLWithColumnsAndWhere(sqlString);
	}

	static void parseSQL(String sql) throws JSQLParserException {
		Statements stmt = CCJSqlParserUtil.parseStatements(sql);
		Statement statement = CCJSqlParserUtil.parse(sql);
		Select selectStatement = (Select) statement;
		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
		List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
		System.out.println(tableList.toString());
	}

	static void parseSQL1(String sql) throws JSQLParserException {
//		Select select = (Select) CCJSqlParserUtil.parse(sql);
//		final AddAliasesVisitor instance = new AddAliasesVisitor();
//		select.getSelectBody().accept(instance);
//		System.out.println(select.toString());
		
//		Select stmt = (Select) CCJSqlParserUtil.parse("SELECT col1 AS a, col2 AS b, col3 AS c "
//	            + "FROM table1 WHERE where col1 in (select col4 from table4)");
		Select stmt = (Select) CCJSqlParserUtil.parse(sql);
	    Map<String, Expression> map = new HashMap<>();        
	    for (SelectItem selectItem : ((PlainSelect)stmt.getSelectBody()).getSelectItems()) {
	        selectItem.accept(new SelectItemVisitorAdapter() {
	            @Override
	            public void visit(SelectExpressionItem item) {
	            	if(item.getAlias()!=null){
	            		map.put(item.getAlias().getName(), item.getExpression());
	            	}else{
	            		map.put(item.getExpression().toString(), item.getExpression());
	            	}
	            }
	        });
	    }

	    System.out.println("map " + map); 
		
	}

	static void parseSQLWithColumns(String sql) throws JSQLParserException{
//		CaseExpression caseExp =  
		Statement stmt2 = CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true).withAllowComplexParsing(true));
	
		List<SelectItem> selectlist = ((PlainSelect) ((Select) stmt2).getSelectBody()).getSelectItems();
		System.out.println("List of  columns in select query");
		System.out.println("--------------------------------");
		List<SelectItem> selectCols = ((PlainSelect) ((Select) stmt2).getSelectBody()).getSelectItems();
		for (SelectItem selectItem : selectCols)
		   System.out.println(selectItem.toString());
	}

	static void parseSQLWithColumnsAndWhere(String sql) throws JSQLParserException {
		Statement select = (Statement) CCJSqlParserUtil.parse(sql);
		String whereCondition = ((PlainSelect) ((Select) select).getSelectBody()).getWhere().toString();
		System.out.println("Where condition: " + whereCondition);
	}

	static void parseSQLWithColumnsAndCase(String sql) throws JSQLParserException {
		Statement select = (Statement) CCJSqlParserUtil.parse(sql);
		String whereCondition = ((PlainSelect) ((Select) select).getSelectBody()).getWhere().toString();
		System.out.println("Where condition: " + whereCondition);
	}
	
	static void parseSQLComplex() throws JSQLParserException{
		String sql = "SELECT * FROM  ( ( SELECT TBL.ID AS rRowId, TBL.NAME AS name, TBL.DESCRIPTION as description, TBL.TYPE AS type, TBL1.SHORT_NAME AS shortName  FROM ROLE_TBL TBL WHERE ( TBL.TYPE = 'CORE' OR  TBL1.SHORT_NAME = 'TNG' AND  TBL.IS_DELETED <> 1  ) ) MINUS ( SELECT TBL.ID AS rRowId, TBL.NAME AS name, TBL.DESCRIPTION as description, TBL.TYPE AS type, TBL3.SHORT_NAME AS shortName,TBL3.NAME AS tenantName FROM ROLE_TBL TBL INNER JOIN TYPE_ROLE_TBL TBL1 ON TBL.ID=TBL1.ROLE_FK LEFT OUTER JOIN TNT_TBL TBL3 ON TBL3.ID = TBL.TENANT_FK LEFT OUTER JOIN USER_TBL TBL4 ON TBL4.ID = TBL1.USER_FK WHERE ( TBL4.ID =771100 AND  TBL.IS_DELETED <> 1  ) ) ) ORDER BY name ASC";

	    System.out.println("using TableNamesFinder to get column names");
	    Statement statement = CCJSqlParserUtil.parse(sql);
	    Select selectStatement = (Select) statement;
	    TablesNamesFinder tablesNamesFinder = new TablesNamesFinder() {
	        @Override
	        public void visit(Column tableColumn) {
	            System.out.println(tableColumn);
	        }
	    };
	    tablesNamesFinder.getTableList(selectStatement);

	    System.out.println("-------------------------------------------");
	    System.out.println("using ast nodes to get column names");
	    SimpleNode node = (SimpleNode) CCJSqlParserUtil.parseAST(sql);

	    node.jjtAccept(new CCJSqlParserDefaultVisitor() {
	        @Override
	        public Object visit(SimpleNode node, Object data) {
	            if (node.getId() == CCJSqlParserTreeConstants.JJTCOLUMN) {
	                System.out.println(node.jjtGetValue());
	                return super.visit(node, data);
	            } else {
	                return super.visit(node, data);
	            }
	        }
	    }, null);
	}

}
