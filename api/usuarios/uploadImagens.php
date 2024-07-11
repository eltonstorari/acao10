<?PHP

include "../conexao.php";

$json['img']=array();

	
	if(isset($_POST["btn"])){
		
		
		
		$rota="imagens";
		$arquivo=$_FILES['imagem']['tmp_name'];
		echo 'Arquivo';
		echo '<br>';
		echo $arquivo;
		$nomeArquivo=$_FILES['imagem']['name'];
		echo 'Nome Arquivo';
		echo '<br>';
		echo $nomeArquivo;
		move_uploaded_file($arquivo,$rota."/".$nomeArquivo);
		$rota=$rota."/".$nomeArquivo;
		$id=$_POST['id'];
		$nome=$_POST['nome'];
		$email=$_POST['email'];
		$senha=$_POST['senha'];
		$nivel=$_POST['nivel'];

		
		echo '<br>';
		echo 'ID: ';
		echo $id;
		echo '<br>';
		echo 'Nome: ';
		echo $nome;
		echo '<br>';
		echo 'Email: ';
		echo $email;
		echo '<br>';
		echo 'senha: ';
		echo $senha;
		echo 'nivel: ';
		echo $nivel;
		echo '<br>';
		echo 'rota :';
		echo $rota;
		echo '<br>';
		echo 'Tipo Imagem: ';
		echo ($_FILES['imagem']['type']);
		echo '<br>';
		echo '<br>';
		echo "Imagem: <br><img src='$rota'>";
		echo '<br>';
		echo '<br>';
		echo 'imagem en Bytes: ';
		echo '<br>';
		echo '<br>';
		
		echo '<br>';
		
		$bytesArquivo=file_get_contents($rota);
		$sql="INSERT INTO usuarios(id,nome,email,senha,nivel,imagem,url_imagem) VALUES (?,?,?,?,?,?,?)";
		$stm=$conexao->prepare($sql);
		$stm->bind_param('issssss',$id,$nome,$email,$senha,$nivel,$bytesArquivo,$rota);
		
		if($stm->execute()){
			echo 'imagem Inserida com Sucesso ';
			$consulta="select * from usuarios where id='{$id}'";
			$resultado=mysqli_query($conexao,$consulta);
			echo '<br>';
			while ($row=mysqli_fetch_array($resultado)){
				echo $row['id'].' - '.$row['nome'].'<br/>';
				array_push($json['img'],array('id'=>$row['id'],'nome'=>$row['nome'], 'email'=>$row['email'],
				 'senha'=>$row['senha'],
				 'nivel'=>$row['nivel'],'foto'=>base64_encode($row['nome']),'rota'=>$row['url_imagem']));
			}
			mysqli_close($conexao);
			
			echo '<br>';
			echo 'Objeto JSON 2';
			echo '<br>';
			echo json_encode($json);
		}
	}
?>