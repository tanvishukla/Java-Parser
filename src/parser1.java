import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import net.sourceforge.plantuml.SourceStringReader;
import java.io.FileOutputStream;

public class parser1 {

	//An array for standard known data types in Java
	public static String[] types = new String[]{"int","String","void","boolean","char","float","int[]","String[]"};	

	//Index for array of objects for Association
	static int objCount=0;

	//block statment variable for body of a class function
	static BlockStmt body=null;

	//Index for array of objects for Dependency
	static int objCount1=0;

	static int objCount2=0;

	//Array for storing UML input for dependent classes and its count
	static String[] ImpArray=new String[20];
	static int ImpCount=0;


	//Array for storing reverse association and its count
	static String[] reverse=new String[20];
	static int revCount=0;


	//Array for storing associated classes and its count
	static String[] ImpArray1=new String[20];
	static int ImpCount1=0;


	//Array for storing constructor items and its count
	static String[] constArray = new String[20];
	static int constCount=0;

	public static int objCount3=0;

	//Array of objects for association between classes
	static Association[] asso;

	//Array of objects for dependency betweenn classes
	static Association[] depend;

	//Array of objects for inheritance between classes
	static Inheritance[] extend;

	//Array of objects for implementation between classes
	static Inheritance[] implement;

	//for gettter setter
	public static ArrayList<String> privateAttributes = new ArrayList();

	//input string for plantuml
	static String umlLine="@startuml\n";

	//name of present class
	public static String className;

	//ArrayList to store all the classes in it
	public static ArrayList<String> allClasses = new ArrayList();

	//ArrayList to store all the interfaces in it
	public static ArrayList<String> allInterfaces = new ArrayList();


	//Main function
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub

		String directory_path=args[0];
		String output_name=args[1];

		//folder which conatins all the java files
		File dir = new File(directory_path);

		//file where all the java files are combined 
		File newfile=new File(directory_path+"/input.txt");

		//array for storing all the files in the folder	
		File[] directoryListing = dir.listFiles();

		//if folder contains files
		if (directoryListing != null) {


			for (File child : directoryListing) {

				//check if the files are java files
				if(child.getName().endsWith(".java")){

					try {
						FileReader fr=new FileReader(child.getAbsolutePath());
						FileWriter fw=new FileWriter(newfile.getAbsolutePath(),true);
						BufferedReader br=new BufferedReader(fr);
						PrintWriter pw=new PrintWriter(fw);
						String line;

						while((line =br.readLine())!=null){

							//skip lines which contains import and package
							if(line.startsWith("import")||line.startsWith("package"))
							{/*skip these lines*/}

							//write all the other lines to the new file
							else
								pw.println(line);

						}pw.println();

						br.close();
						pw.close();

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				//if files with other extensions are present then do nothing
				else{ /* do nothing*/ }

			}
		} else {

			/*Enter correct directory path*/
		}

		//objects for association and dependency
		asso=new Association[10];
		depend=new Association[10];
		extend= new Inheritance[10];
		implement=new Inheritance[10];

		//instruction for PlantUML to display modifiers as expected in a class diagram
		umlLine=umlLine+"skinparam classAttributeIconSize 0\n";

		//For reading from file
		FileInputStream in = new FileInputStream(newfile.getAbsolutePath());

		//Compilation unit for Java Parser
		CompilationUnit cu;
		try {

			cu = JavaParser.parse(in);
		} finally {
			in.close();
		}

		//visits the class
		new ClassVisitor().visit(cu, asso);

		//When interface is up and sub classes are down
		for(int h=0;h<objCount3;h++){


			for(int j=0;j<allClasses.size();j++){

				if(allClasses.get(j).toString().equals(implement[h].Super))
				{	

					umlLine=umlLine+"interface "+implement[h].Super+"<|.. class "+implement[h].Sub+"\n";

				}

			}

		}

		//For dependency
		for(int h=0;h<ImpCount;h++){


			for(int j=0;j<allInterfaces.size();j++){
				int abc=ImpArray[h].indexOf("<");
				int pqr=ImpArray[h].indexOf(".");
				String s1=ImpArray[h].substring(0, abc-1);
				String s2=ImpArray[h].substring(pqr+3);

				//if the dependency is on an interface
				if(s1.indexOf(allInterfaces.get(j))!=-1)
				{	

					for(int k=0;k<allInterfaces.size();k++){

						if(s2.indexOf(allInterfaces.get(k))!=-1)
						{	//do nothing 
							break;
						}
						else
						{

							if(umlLine.indexOf(ImpArray[h])!=-1)
							{
								/*Already present. Do nothing*/

							}
							//Dependency not present
							else
							{	
								umlLine=umlLine+ImpArray[h]+"\n"; 

							}

						}

					}

				}

				else
				{/*Dependency is not on an interface*/}

			}

		}


		//For association
		for(int h=0;h<ImpCount1;h++){

			for(int j=0;j<allClasses.size();j++){

				int abc=ImpArray1[h].indexOf("-");
				String s1=ImpArray1[h].substring(0, abc);
				//if occured as a class/interface
				if(s1.indexOf(allClasses.get(j))!=-1)
				{	

					if(umlLine.indexOf(ImpArray1[h])!=-1)
					{
						/*association already present*/

					}
					//Write association
					else
						umlLine=umlLine+ImpArray1[h]+"\n";
				}    		
			}

		}


		//For constructor
		for(int h=0;h<constCount;h++){

			for(int j=0;j<allInterfaces.size();j++){

				int abc=constArray[h].indexOf("<");
				int pqr=constArray[h].indexOf(".");
				String s1=constArray[h].substring(0, abc);
				String s2=constArray[h].substring(pqr+3);
				//if the dependency is on an interface
				if(s1.indexOf(allInterfaces.get(j))!=-1)
				{	


					for(int k=0;k<allInterfaces.size();k++)
					{

						if(s2.indexOf(allInterfaces.get(k))!=-1)
						{	/*do nothing*/ }
						else
						{
							if(umlLine.indexOf(constArray[h])!=-1)
							{
								/*Dependency already present.*/

							}

							//Write dependency
							else{

								umlLine=umlLine+constArray[h]+"\n";
							}
						}

					}

				}
				else{
					//is not an interface
					break;

				}

			}

		}


		//For extends
		for(int h=0;h<objCount2;h++){


			for(int j=0;j<allClasses.size();j++){


				if(allClasses.get(j).toString().equals(extend[h].Super))
				{	
					umlLine=umlLine+"class "+extend[h].Super+"<|-- class "+extend[h].Sub+"\n";

				}
				else
				{	/*do something*/ }
			}
		}


		//EOF for plantUML input
		umlLine=umlLine+"@enduml";

		//for writing the output from string to plantUML 
		ByteArrayOutputStream UMLfile = new ByteArrayOutputStream();
		SourceStringReader umlread=new SourceStringReader(umlLine);
		String plantuml_input=umlread.generateImage(UMLfile);
		byte[] umlData = UMLfile.toByteArray();
		InputStream image1 = new ByteArrayInputStream(umlData);
		BufferedImage umlImage = ImageIO.read(image1);

		//For checking the extension of the required output image
		if(output_name.contains(".jpg")||output_name.contains("jpeg"))

			ImageIO.write(umlImage,"jpg",new File(directory_path+"/"+output_name));

		else if(output_name.contains("png"))
			ImageIO.write(umlImage,"png",new File(directory_path+"/"+output_name));	

		else if(output_name.contains("pdf"))
			ImageIO.write(umlImage,"pdf",new File(directory_path+"/"+output_name));


		newfile.delete();
	}


	//Class for assoication & dependency	
	public class Association{

		String main_class;
		String asso_class;
		String obj_name;
		String multiplicity;
		String line;

	}

	//Class for extends and implements
	public class Inheritance{

		String Super;
		String Sub;

	}

	//Visits the class in the Java source file
	private static class ClassVisitor extends VoidVisitorAdapter {


		//method of VoidVisitorAdapter inherited    	
		public void visit(ClassOrInterfaceDeclaration n, Object arg) throws NullPointerException {

			String subclass=null;
			String superclass=null;

			//Get name of current class
			className=n.getName();

			//object of main outer class
			parser1 p =new parser1();

			//initialize the array to index of 0
			extend[objCount2]=p.new Inheritance();

			//If a class extends another class
			if(n.getExtends()!=null)
			{	
				//add all the details to the array
				extend[objCount2].Sub=className;
				extend[objCount2].Super=n.getExtends().get(0).toString();
				objCount2++;

			}

			//If a class implements another interface
			if(n.getImplements()!=null)
			{	


				List <ClassOrInterfaceType> intfe = n.getImplements();

				for(ClassOrInterfaceType c : intfe){ 
					implement[objCount3]=p.new Inheritance();
					implement[objCount3].Sub=className;
					implement[objCount3].Super=c.getName();
					objCount3++;	
				}

			}

			//defining an interface		
			if(n.isInterface())
			{	umlLine=umlLine+"interface "+n.getName()+"{\n";
			allInterfaces.add(n.getName());
			allClasses.add(n.getName());

			}

			//defining a class
			else
			{	
				umlLine=umlLine+"class "+n.getName()+"{\n";
				allClasses.add(className);
			}

			new VariableVisitor().visit(n,null);
			new MethodVisitor().visit(n, null);
			new ConstructorVisitor().visit(n,null);
			umlLine=umlLine+"\n}\n";

		}  

	}

	private static class ConstructorVisitor extends VoidVisitorAdapter{

		//Visits constructor of the class
		public void visit(ConstructorDeclaration n, Object arg){

			String modifiers=null;
			if(n.getModifiers()==1||n.getModifiers()==0)
				modifiers="+";
			else if(n.getModifiers()==2)
				modifiers="-";
			else if(n.getModifiers()==4)
				modifiers="#";

			//if no parameters in the constructor
			if(n.getParameters()==null)
			{	 
				umlLine=umlLine+modifiers+" "+className+"()\n";

			}
			//if parameters present in the constructor
			else
			{
				//get datatype of the parameter
				String type=n.getParameters().get(0).getType().toString();
				for(int m=0;m<types.length;m++){
					// if type matches standard data type
					if(n.getParameters().get(0).getType().toString().equals(types[m]))
					{
						/*do nothing*/
					}
					//if not a standard data type
					else
					{ 
						//if modifier other than protected then add it to the constructor array
						if(modifiers!="#")   
						{	
							constArray[constCount]=n.getParameters().get(0).getType().toString()+" <.. "+className;
							constCount++;
							umlLine=umlLine+"+"+className+"("+n.getParameters().get(0).getId().toString()+" : "+type+") \n";
							break;
						}

						else
						{/*do nothing*/}
					}

				}	  


			}

		}


	}

	private static class MethodVisitor extends VoidVisitorAdapter {
		//Visits methods of the class
		public void visit(MethodDeclaration n, Object arg) {
			parser1 p =new parser1();
			depend[objCount1]=p.new Association();
			int dep=0;	

			String modifiers=null;
			if(n.getModifiers()==1||n.getModifiers()==0)
				modifiers="+";
			else if(n.getModifiers()==2)
				modifiers="-";
			else if(n.getModifiers()==4)
				modifiers="#";
			//if no parameters present in method declaration
			if(n.getParameters()==null)
			{	 //if method not private
				if(modifiers!="-")

					umlLine=umlLine+modifiers+" "+n.getName()+" : "+n.getType()+"()\n";

				else
				{/*a private method*/}

			}
			// if parameters present in the method declaration
			else{  
				//if method is static
				if(n.toString().contains("static")){

					for(int m=0;m<types.length;m++){
						//check if datatype is a standard data type
						if(n.getParameters().get(0).getType().toString().equals(types[m]))
						{
							String var=n.getParameters().get(0).getId().toString(); 
							String type=n.getParameters().get(0).getType().toString();
							umlLine=umlLine+n.getName()+" ("+var+" : "+type+") : "+n.getType()+"\n";

						}

					}

				}
				// method is not static
				else		
				{
					for(int m=0;m<6;m++){
						//check if datatype is a standard data type
						if(n.getParameters().get(0).getType().toString().equals(types[m]))
						{
							String var=n.getParameters().get(0).getId().toString(); 
							String type=n.getParameters().get(0).getType().toString();
							//if not a private method with parameters
							if(modifiers!="-")
								umlLine=umlLine+modifiers+" "+n.getName()+" ("+var+" : "+type+") : "+n.getType()+"\n";

							else
							{/*a private method*/}
						}
						//type does not match standard data type
						else
						{   
							dep=1;

						}
					}	  
					// not a standard data type
					if (dep==1){
						//get type
						String type=n.getParameters().get(0).getType().toString();
						ImpArray[ImpCount]=n.getParameters().get(0).getType().toString()+" <.. "+className ;
						ImpCount++;
						//if not a private method
						if(modifiers!="-")
							umlLine=umlLine+ modifiers+n.getName()+"("+n.getParameters().get(0).getId().toString()+" : "+type+") \n ";
						else
						{/*a private method*/}
					}   

				}

			}
			//Parses method body
			if(n.getBody()!=null)
			{
				body=n.getBody();

				if(body.getStmts()==null){
					/*Body is empty*/	
				}
				//if method body not empty
				else{

					String s1[],str[];	
					//take all method body in a list
					List<Statement> l= body.getStmts();

					for(Statement st:l){
						if(st.toString().contains("= new")){
							s1=st.toString().split(" "); 
							ImpArray[ImpCount]=s1[0]+" <.. "+className;
							ImpCount++;
							break;
						}
						//if method is a setter method
						else if(st.toString().contains("this")){

							s1=st.toString().split("=");

							if(s1.length>1){

								str=s1[0].split("this.");
								if(str.length>1)
								{	
									if(umlLine.contains(str[1]))
									{	 
										umlLine=umlLine.replace("- "+str[1], "+ "+str[1]);
										umlLine=umlLine.replace("+"+n.getName()+"("+n.getParameters().get(0).getId().toString()+" : "+n.getParameters().get(0).getType().toString()+")"," ");
									}
									else
									{	/*getter setter not present*/}

								}
								else
								{/*nothing present*/}
							}
							else
							{/*nothing present*/}
						} 

						else{
							/*no new reference*/

						}

					}

				}
			}

			else{

				/*nothing is present*/
			}

		}    	  

	}


	private static class VariableVisitor extends VoidVisitorAdapter {

		//Visits the variables of the class
		public void visit(FieldDeclaration n, Object arg) {
			parser1 p =new parser1();
			asso[objCount]=p.new Association();
			int flag=0;   		

			String modifiers=null;
			if(n.getModifiers()==1)
				modifiers="+";
			else if(n.getModifiers()==2)
				modifiers="-";
			else if(n.getModifiers()==4)
				modifiers="#";
			else if(n.getModifiers()==0)
				modifiers="~";

			//check data type of variable
			for(int j=0;j<5;j++){	
				//equals standard data type
				if(n.getType().toString().equals(types[j])||n.getType().toString().equals(types[j]+"[]")){
					flag=0;	
					break;
				}

				else
					flag=1;
			}
			//standard data type
			if(flag == 0) {

				int count=n.getVariables().size();
				//variables not of protected or package type
				if(modifiers!="#" && modifiers!="~"){
					for(int i=0;i<count;i++)
					{
						if(n.getVariables().get(i).getId().toString().contains("]"))

							umlLine=umlLine+modifiers+" "+n.getVariables().get(i).getId().getName()+" : "+n.getType()+"[]\n";

						else		
						{
							if(n.getType().toString().equals("int[]"))
								umlLine=umlLine+modifiers+" "+n.getVariables().get(i).getId().getName()+" : int(*)\n";

							else
								umlLine=umlLine+modifiers+" "+n.getVariables().get(i).getId().getName()+" : "+n.getType()+"\n";
						}
					}	

				}

				else
				{/*a protected or package variable*/}

			}      
			// not a standard data type
			else if(flag == 1){

				String str=n.getType().toString();
				String putAsso;
				int rev=0;
				//if collection is present
				if(str.contains("<"))
				{   
					putAsso = str.substring(str.toString().indexOf("<")+1, str.toString().indexOf(">"));
					for(int u=0;u<ImpCount1;u++){

						if(ImpArray1[u].contains(putAsso+" -- "+'"'+"*"+'"'+" "+className)||ImpArray1[u].contains(className+" -- "+'"'+"*"+'"'+" "+putAsso))
						{
							rev=1;
							break;

						}
						else
						{rev=0;}

					}
					if(rev==0){
						ImpArray1[ImpCount1]=className+" -- "+'"'+"*"+'"'+" "+putAsso+"\n" ;
						ImpCount1++;
					}
				}
				//if collection is not present
				else{

					for(int u=0;u<ImpCount1;u++){

						if(ImpArray1[u].contains(n.getType().toString()+" -- "+'"'+"1"+'"'+" "+className)||ImpArray1[u].contains(className+" -- "+'"'+"1"+'"'+" "+n.getType().toString())||ImpArray1[u].contains(n.getType().toString()+" -- "+'"'+"*"+'"'+" "+className)||ImpArray1[u].contains(className+" -- "+'"'+"*"+'"'+" "+n.getType().toString()))
						{   
							rev=1;
							break;
						}

						else
						{	   rev=0;

						}

					}

					if(rev==0){
						ImpArray1[ImpCount1]=className+" -- "+'"'+"1"+'"'+" "+n.getType().toString();
						ImpCount1++;
					}

					if(rev==1){

						/*reverse present for normal*/

					}

				} 


			}   	   


		}


	}      

}


