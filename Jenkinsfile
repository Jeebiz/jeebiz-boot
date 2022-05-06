pipeline {
  agent any
  stages {
    stage('检出') {
      steps {
        checkout([$class: 'GitSCM',
        branches: [[name: GIT_BUILD_REF]],
        userRemoteConfigs: [[
          url: GIT_REPO_URL,
          credentialsId: CREDENTIALS_ID
        ]]])
      }
    }
    stage('编译') {
      steps {
        withCredentials([
          usernamePassword(
            // CODING 持续集成的环境变量中内置了一个用于上传到当前项目制品库的凭证
            credentialsId: "${CODING_ARTIFACTS_CREDENTIALS_ID}",
            usernameVariable: 'CODING_ARTIFACTS_USERNAME',
            passwordVariable: 'CODING_ARTIFACTS_PASSWORD'
          )]) {
            withEnv([
                  "CODING_ARTIFACTS_USERNAME=${CODING_ARTIFACTS_USERNAME}",
                  "CODING_ARTIFACTS_PASSWORD=${CODING_ARTIFACTS_PASSWORD}"
              ]) {
                sh 'mvn package -U -P test -Dmaven.test.skip=true -s ./settings.xml'
              }
            }
          }
        }
        stage('构建镜像并推送到 CODING Docker 制品库') {
          steps {
            sh "docker build -t ${CODING_DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION} -f ${DOCKERFILE_PATH} ${DOCKER_BUILD_CONTEXT}"
            sh "docker tag ${CODING_DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION} ${CODING_DOCKER_IMAGE_NAME}:latest"
            useCustomStepPlugin(key: 'coding-public:artifact_docker_push', version: 'latest', params: [image:"${CODING_DOCKER_IMAGE_NAME}:latest",repo:"${DOCKER_REPO_NAME}"])
          }
        }
        stage('部署至Kubernetes 集群') {
          steps {
            cdDeploy(deployType: 'PATCH_IMAGE', application: '${CCI_CURRENT_TEAM}', pipelineName: '${PROJECT_NAME}-${CCI_JOB_NAME}-2217328', image: '${CODING_DOCKER_REG_HOST}/${CODING_DOCKER_IMAGE_NAME}:latest', cloudAccountName: 'huaweicloud', namespace: 'dy-test', manifestType: 'Deployment', manifestName: 'tianyin-edu-third-deploy', containerName: 'tianyin-edu-third', credentialId: '764068872ea6448b813bbac7e17b34ee', personalAccessToken: '${CD_PERSONAL_ACCESS_TOKEN}')
          }
        }
      }
      environment {
        DOCKER_REPO_NAME = 'docker'
        DOCKERFILE_PATH = 'Dockerfile'
        DOCKER_BUILD_CONTEXT = '.'
        SERVICE_NAME = '${DEPOT_NAME}_${GIT_LOCAL_BRANCH:-branch}'
        DOCKER_IMAGE_VERSION = '${GIT_LOCAL_BRANCH:-branch}-${GIT_COMMIT_SHORT}'
        CODING_DOCKER_REG_HOST = "${CCI_CURRENT_TEAM}-docker.pkg.${CCI_CURRENT_DOMAIN}"
        CODING_DOCKER_IMAGE_NAME = "${PROJECT_NAME.toLowerCase()}/${DOCKER_REPO_NAME}/${DEPOT_NAME}"
        CODING_MAVEN_REPO_ID = "${CCI_CURRENT_TEAM}-${PROJECT_NAME}-${MAVEN_REPO_NAME}"
        CODING_MAVEN_REPO_URL = "${CCI_CURRENT_WEB_PROTOCOL}://${CCI_CURRENT_TEAM}-maven.pkg.${CCI_CURRENT_DOMAIN}/repository/${PROJECT_NAME}/${MAVEN_REPO_NAME}/"
      }
    }