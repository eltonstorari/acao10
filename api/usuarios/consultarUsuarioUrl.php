<?PHP

include "../conexao.php";

$json=array();

	if (isset($_GET["id"])) {
		$id = $_GET["id"];
		
		$consulta = "SELECT * FROM usuarios where id = '{$id}'";
		$resultado = mysqli_query($conexao, $consulta);

		if ($registro=mysqli_fetch_array($resultado)) {
			$result["id"]=$registro['id'];
			$result["nome"]=$registro['nome'];
			$result["email"]=$registro['email'];
			$result["senha"]=$registro['senha'];
			$result["nivel"]=$registro['nivel'];
			$result["url_imagem"] = $registro['url_imagem'];
			$json['usuario'][]=$result;
		}else{
			$resultar["id"]='Nenhum registro encontrado com base no id';
			$resultar["nome"]='';
			$resultar["email"]='';
			$resultar["senha"]='';
			$resultar["nivel"]='';
			$resultar["url_imagem"]='';
			$json['usuario'][]=$resultar;

		}


	}
	 else{

	 		$resultar["id"]='Isset -> false';
			$resultar["nome"]='';
			$resultar["email"]='';
			$resultar["senha"]='';
			$resultar["nivel"]='';
			$resultar["url_imagem"]='';
			$json['usuario'][]=$resultar;


	 }

	 	mysqli_close($conexao);
		echo json_encode($json);

	?>