<?PHP

include "../conexao.php"; 

$json=array();

	if (isset($_GET["id"])) {
		$id = $_GET["id"];
		
		$consulta = "SELECT * FROM clubes where id = '{$id}'";
		$resultado = mysqli_query($conexao, $consulta);

		if ($registro=mysqli_fetch_array($resultado)) {
			$result["id"]=$registro['id'];
			$result["nome"]=$registro['nome'];
			$result["descricao"]=$registro['descricao'];
			$result["telefone"]=$registro['telefone'];
			$result["whatsapp"]=$registro['whatsapp'];
			$result["cep"]=$registro['cep'];
			$result["cidade"]=$registro['cidade'];
			$result["bairro"]=$registro['bairro'];
			$result["rua"]=$registro['rua'];
			$result["numero"]=$registro['numero'];
			$result["email"]=$registro['email'];
			$result["url_logo"] = $registro['url_logo'];
			$json['clube'][]=$result;
		}else{
			$resultar["id"]='Nenhum registro encontrado com base no id';
			$resultar["nome"]='';
			$resultar["descricao"]='';
			$resultar["telefone"]='';
			$resultar["whatsapp"]='';
			$resultar["cep"]='';
			$resultar["cidade"]='';
			$resultar["bairro"]='';
			$resultar["rua"]='';
			$resultar["numero"]='';
			$resultar["email"]='';
			$resultar["url_logo"]='';
			$json['clube'][]=$resultar;

		}


	}
	 else{

	 		$resultar["id"]='Isset -> false';
			 $resultar["nome"]='';
			 $resultar["descricao"]='';
			 $resultar["telefone"]='';
			 $resultar["whatsapp"]='';
			 $resultar["cep"]='';
			 $resultar["cidade"]='';
			 $resultar["bairro"]='';
			 $resultar["rua"]='';
			 $resultar["numero"]='';
			 $resultar["email"]='';
			 $resultar["url_logo"]='';
			$json['clube'][]=$resultar;


	 }

	 	mysqli_close($conexao);
		echo json_encode($json);

	?>