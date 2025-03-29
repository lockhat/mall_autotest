pipeline {
    agent any

    tools {
        maven 'Maven3'       // Jenkins 全局 Maven 名称
        allure 'Allure'      // Jenkins 全局 Allure 名称
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

        stage('📊 生成 Allure 报告') {
            steps {
                echo "生成 Allure 报告"
                step([$class: 'AllureReportBuildStep', results: [[path: 'target/allure-results']]])
            }
        }
    }

    post {
        success {
            echo "✅ 测试成功，Allure 报告已生成"
        }
        failure {
            echo "❌ 测试失败，请查看 Allure 报告"
        }
    }
}
