<?php

include "../conexao.php"; 

$json = array();

if (isset($_GET["id"])) {
		$id = $_GET["id"];

		$consulta = "SELECT * FROM usuarios WHERE id =  '{$id}'";
		$resultado  = mysqli_query($conexao, $consulta);


		if ($registro = mysqli_fetch_array($resultado)) {
			$result["id"] = $registro['id']; 
			$result["nome"] = $registro['nome'];
			$result["email"] = $registro['email'];
			$result["senha"] = $registro['senha'];
			$result["nivel"] = $registro['nivel'];
			$result["url_imagem"] = $registro['url_imagem'];
			$json['usuarios'][] = $result;
			
		}else{

			$resultar["id"] = 0; 
			$resultar["nome"] = 'Código não existe no banco!';
			$resultar["email"] = 'Não Registro';
			$resultar["senha"] = 'Não Registro';
			$resultar["imagem"] = 'nao registrado';
			$json["usuarios"][] = $resultar;


		}
		mysqli_close($conexao);
		echo json_encode($json);
	
	}else{
		$resultar["codigo"] = 0; 
			$resultar["nome"] = 'Não Registro';
			$resultar["categoria"] = 'Não Registro';
			$resultar["professor"] = 'Não Registro';
			$resultar["imagem"] = 'nao registrado'; 
			$json["usuarios"][] = $resultar;
			echo json_encode($json);

	}

?>


