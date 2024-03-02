FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["DotNetAPI.csproj", "."]
RUN dotnet restore "./DotNetAPI.csproj"
COPY . .
WORKDIR "/src/."
RUN dotnet build "DotNetAPI.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "DotNetAPI.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "DotNetAPI.dll"]
