<?php

// Banco Local 

function conectar(){
	
	$host = "localhost";
	$db = "db_acao10";
	$user = "root";
	$senha = "";

	$con = new mysqli($host, $user, $senha, $db);
	return $con;
}

$conexao = conectar();


?>