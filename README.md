# graalvm-aws-lambda

A GraalVm optimized AWS Lambda function

## Build for native lambda image

1. Build the docker image

    docker build . -t graalbuildimage

1. Run the container and mount the sources

   docker run -v "$(pwd):/src" -it graalvmbuildimage

1. Go to the source folder and continue with native build

   cd src


## Native Build 

   mvn clean

   mvn package

Leave the container (exit)

Rename the Bootrap executable to lowercase and put it in a bootstrap.zip file. Or just call

   prepareZip.sh

## Add your domain

Open the template.yaml and configure your domain and ssl certificate

## Deploy

You need to have aws cli configured in your shell and sam installed
https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-getting-started.html

   sam package --template-file template.yaml --output-template-file packaged.yaml --s3-bucket {YOUR-BUCKET-NAME}
   
   sam publish --template packaged.yaml --region eu-central-1
   
   sam deploy --s3-bucket {YOUR-BUCKET-NAME} --capabilities CAPABILITY_IAM

### Test

Go to api gateway in the s3 console and search for your cloudfront domain 

   https://{ID}.execute-api.eu-central-1.amazonaws.com/prod/hello 

If you want to test my integration

   curl -XGET https://graalvmlambda.christianoette.com/hello

## Performance Results

A cold start test within Api gateway takes me less then 300ms.  

I haven't tested it with a bigger java function yet

## Known issues

The demo is not fully finished. Feel free to contribute!