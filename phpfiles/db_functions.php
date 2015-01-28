<?php
 
class DB_Functions {
 
    private $db;
 
    //put your code here
    // constructor
    function __construct() {
        include_once './db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
 
    }
 
    /**
     * Storing new location
     * returns location details
     */
    public function storeLocation($Id,$Title,$Snippet,$Position,$Group) {
        // Insert location into database
        $result = mysql_query("INSERT INTO locations VALUES($Id,'$Title','$Snippet','$Position','$Group')");
 
        if ($result) {
            return true;
        } else {
            if( mysql_errno() == 1062) {
                // Duplicate key - Primary Key Violation
                return true;
            } else {
                // For other errors
                return false;
            }            
        }
    }
     /**
     * Getting all locations
     */
    public function getAllLocations() {
        $result = mysql_query("select * FROM locations");
        return $result;
    }
	
	/**
     * Getting sel locations
     */
    public function getSelLocations($groups_sel) {
        //$result = mysql_query("select * FROM locations WHERE Group='$groups_sel'");
        $result = mysql_query("select * FROM locations");
        return $result;
    }
}
 
?>
