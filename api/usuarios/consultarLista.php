<?php 

include "../conexao.php";

$json = array();


$consulta = "SELECT id, nome, email, senha, nivel, url_imagem FROM usuarios";
$resultado = mysqli_query($conexao, $consulta);

while($registro = mysqli_fetch_array($resultado)){
	$json['usuario'][] = $registro;
	//echo $registro['id']. ' - ' .$registro['nome']. ' - ' .$registro['email']. ' - ' .$registro['senha']. ' - ' .$registro['nivel']. '<br/>';


	
}

	mysqli_close($conexao);
	echo json_encode($json);



?>