pipeline {
    agent any

    tools {
        maven 'Maven3'
        allure 'Allure'
    }

    environment {
        GIT_REPO = 'git@github.com:lockhat/mall_autotest.git'
    }

    stages {
        stage('📥 拉取代码') {
            steps {
                git branch: 'master', url: "${GIT_REPO}", credentialsId: 'github-ssh'
            }
        }

        stage('🔨 构建 & 执行测试') {
            steps {
                echo "开始执行接口测试"
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            echo "🧪 使用 CLI 方式生成 Allure 报告"
            sh '''
                allure generate target/allure-results -o target/allure-report --clean || true
            '''
            // 可以选择将 HTML 报告归档
            archiveArtifacts artifacts: 'target/allure-report/**', allowEmptyArchive: true
        }
    }
}
