import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateVpcRequest;
import software.amazon.awssdk.services.ec2.model.CreateVpcResponse;
import software.amazon.awssdk.services.ec2.model.CreateInternetGatewayRequest;
import software.amazon.awssdk.services.ec2.model.CreateInternetGatewayResponse;
import software.amazon.awssdk.services.ec2.model.AttachInternetGatewayRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

public class CreateVirtualPrivateCloud {

    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try {
            // Step 1: Create the VPC
            CreateVpcRequest vpcRequest = CreateVpcRequest.builder()
                    .cidrBlock("10.0.0.0/16") // Specify the CIDR block for the VPC
                    .build();

            CreateVpcResponse vpcResponse = ec2.createVpc(vpcRequest);
            String vpcId = vpcResponse.vpc().vpcId();
            System.out.println("VPC created with ID: " + vpcId);

            // Step 2: Create an Internet Gateway
            CreateInternetGatewayRequest igwRequest = CreateInternetGatewayRequest.builder().build();
            CreateInternetGatewayResponse igwResponse = ec2.createInternetGateway(igwRequest);
            String igwId = igwResponse.internetGateway().internetGatewayId();
            System.out.println("Internet Gateway created with ID: " + igwId);

            // Step 3: Attach the Internet Gateway to the VPC
            AttachInternetGatewayRequest attachIgwRequest = AttachInternetGatewayRequest.builder()
                    .internetGatewayId(igwId)
                    .vpcId(vpcId)
                    .build();
            ec2.attachInternetGateway(attachIgwRequest);
            System.out.println("Internet Gateway attached to VPC");

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        } finally {
            ec2.close();
        }
    }
}
