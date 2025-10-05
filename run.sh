#!/bin/bash

# Script para executar o Sistema Plano AEE
# Desenvolvido para APAPEQ

echo "========================================="
echo "    Plano AEE - Sistema APAPEQ v1.0.0"
echo "========================================="
echo ""

# Verifica se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Instale o Java 17 ou superior."
    exit 1
fi

# Verifica versão do Java
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java versão $JAVA_VERSION encontrada. É necessário Java 17 ou superior."
    exit 1
fi

echo "✅ Java $JAVA_VERSION detectado"
echo ""

# Cria diretório de logs se não existir
mkdir -p logs

# Verifica se o Maven está disponível
if command -v mvn &> /dev/null; then
    echo "🚀 Executando via Maven..."
    echo ""
    
    # Compila o projeto
    echo "📦 Compilando projeto..."
    mvn clean compile -q
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilação bem-sucedida"
        echo ""
        
        # Executa a aplicação
        echo "🎯 Iniciando aplicação..."
        mvn javafx:run
    else
        echo "❌ Erro na compilação"
        exit 1
    fi
else
    echo "⚠️  Maven não encontrado. Tentando execução direta..."
    echo ""
    
    # Tenta executar diretamente (se já compilado)
    if [ -f "target/classes/com/planoaee/MainApp.class" ]; then
        echo "🎯 Iniciando aplicação..."
        java --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml -cp "target/classes:target/dependency/*" com.planoaee.MainApp
    else
        echo "❌ Classes não compiladas. Instale o Maven ou compile manualmente."
        echo ""
        echo "Para instalar Maven:"
        echo "sudo apt install maven"
        exit 1
    fi
fi

echo ""
echo "👋 Sistema finalizado. Obrigado por usar o Plano AEE!"


