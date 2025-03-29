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
                git branch: 'main', url: "${GIT_REPO}", credentialsId: 'github-ssh'
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
            echo "📦 执行后动作：生成 Allure 报告（无论成功失败）"
            allure([
              results: [[path: 'target/allure-results']]
            ])
        }

        success {
            echo "✅ 测试成功，Allure 报告已生成"
        }

        failure {
            echo "❌ 测试失败，请查看 Allure 报告"
        }
    }
}
