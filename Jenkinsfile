pipeline {
    agent any

    tools {
        maven 'Maven3'
        allure 'Allure'
    }

    environment {
        GIT_REPO = 'git@github.com:lockhat/mall_autotest.git'
        PYTHON_COVERAGE_CLI = 'python3 scripts/run_coverage.py'

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
                sh 'mvn test -s /var/jenkins_home/settings.xml'
            }
        }
        stage('📊 接口测试覆盖率分析') {
                    steps {
                        echo "开始生成接口覆盖率分析"
                        // url维度 ，statusCode维度
//                         sh '''
//                             mvn exec:java -Dexec.mainClass=com.mycompany.mall.admin.utils.UrlCoverageRunner
//                             mvn exec:java -Dexec.mainClass=com.mycompany.mall.admin.utils.StatusCodeCoverageRunner
//                         '''
                           sh '''
                               ${PYTHON_COVERAGE_CLI} --type all || echo "覆盖率分析失败但不中断流水线"
                           '''
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

            // 归档接口覆盖率报告
            archiveArtifacts artifacts: 'target/coverage/**', allowEmptyArchive: true
        }
    }
}
