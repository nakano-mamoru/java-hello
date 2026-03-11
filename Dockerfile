FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

CMD ["java","-jar","app.jar"]


# # ベースイメージは Temurin 21
# FROM eclipse-temurin:21-jdk-alpine

# # 作業ディレクトリ
# WORKDIR /app

# # Maven Wrapper とソースをコピー
# COPY . .

# # ビルド（jar作成）
# # RUN ./mvnw.cmd clean package -DskipTests

# # 実行するjar
# CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]

# # デフォルトで8080を公開
# EXPOSE 8080
