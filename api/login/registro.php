<?php 

include "../conexao.php";

$json = array();

if(isset($_GET["id"]) && 
    isset($_GET["nome"]) && 
    isset($_GET["email"]) && 
    isset($_GET["senha"]) &&
    isset($_GET["nivel"]) &&
    isset($_GET["url_imagem"]) ){

	$id = $_GET["id"];
        $nome = $_GET["nome"];
        $email = $_GET["email"];
        $senha = $_GET["senha"];
        $nivel = $_GET["nivel"];
        $url_imagem = $_GET["url_imagem"];

        $inserir = "INSERT INTO usuarios (id, nome, email, senha, nivel, url_imagem) VALUES ('{$id}','{$nome}', '{$email}', '{$senha}', '{$nivel}', '{$url_imagem}'  )";

        $resultado_inserir = mysqli_query($conexao, $inserir);

        if ($resultado_inserir) {
            $consulta = "SELECT * FROM usuarios WHERE id = '{$id}'";


            $resultado = mysqli_query($conexao, $consulta);
            if ($registro = mysqli_fetch_array($resultado)) {
                $json['usuarios'][] = $registro;
            }

            mysqli_close($conexao);
            echo json_encode($json);
        
        }else{        
            
            $result["id"] = "Não foi possivel a trazer os DADOS do  CURSO enviado...";
            $result["nome"] = "";
            $result["email"] = "";
            $result["senha"] = "";
            $result["nivel"] = "";
            $json['usuarios'][] = $result;
            echo json_encode($json);

        }

    }else{    

        $result["id"] = "Está faltando informações.... [Não foi possivel INSERIR registro verifique a todos os campos na API.]";
            $result["nome"] = "";
            $result["email"] = "";
            $result["senha"] = "";
            $result["nivel"] = "";
            $json['usuarios'][] = $result;
        echo json_encode($json);    
            

           
            
 

        }

?>