<?php

$name = $_POST['name'];
$username = $_POST['user'];
$password = $_POST['pass'];
$repeatpassword = $_POST['repass'];
$date = date("Y-m-d");

//connect to db
include_once './db_functions_group.php';
$db = new DB_Functions();

$namecheck = mysql_query("SELECT username FROM users WHERE username='$username'");
$count = mysql_num_rows($namecheck);

 if($count !=0){
    die("User with this name already exists");
  }

if($name&&$username&&$password&&$repeatpassword)
{

  if ($password==$repeatpassword){
     if(strlen($username)>25||strlen($name)>25) {  
         echo "Max limit for username/name are 25 characters";  
      }
     else {
         if(strlen($password)>25||strlen($password)<6){
             echo "Password must be between 6 and 25 characters";
         }
         else {
            //register the user!	
             $password = md5($password);
             //$repeatpassword = md5($repeatpassword);	        
             $queryreg = mysql_query("              
			  INSERT INTO users VALUES('','$name','$username','$password','$date');             
             ");		

             $a["status"] = True;
	         echo json_encode($a);			 
        }  
     }  
  } 

else {
  
  echo "Passwords don't match";

}

}
else
  die("Please fill in all fields");

?>
