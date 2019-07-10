Feature: SFTP -> CYR

  Scenario Outline: CRM room type records successfully published to CYR system

    Given Prepare <OUTPUT_FILE> using <INPUT_FILE> where <DATE_FORMAT> was set up for date
    When CRM data <OUTPUT_FILE> was uploaded to SFTP in <FOLDER>
    And execute /request/kibana.json to receive correlataionId for each record in test data
    Then validate and verify sent messages from test data according to schema

    Examples:
      | INPUT_FILE                                 | OUTPUT_FILE                                 | FOLDER             | DATE_FORMAT |
      | /file/RU/in/CYR_input16_RU.csv             | /file/RU/out/CYR_input16_RU.csv             | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_input16Empty_RU.csv        | /file/RU/out/CYR_input16Empty_RU.csv        | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_input34_RU.csv             | /file/RU/out/CYR_input34_RU.csv             | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_input34Empty_RU.csv        | /file/RU/out/CYR_input34Empty_RU.csv        | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_timeOutBoundEmpty_RU.csv   | /file/RU/out/CYR_timeOutBoundEmpty_RU.csv   | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_timeInBoundEmpty_RU.csv    | /file/RU/out/CYR_timeInBoundEmpty_RU.csv    | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_numberOutBoundEmpty_RU.csv | /file/RU/out/CYR_numberOutBoundEmpty_RU.csv | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_numberInBoundEmpty_RU.csv  | /file/RU/out/CYR_numberInBoundEmpty_RU.csv  | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_marketingTrue_RU.csv       | /file/RU/out/CYR_marketingTrue_RU.csv       | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_marketingFalse_RU.csv      | /file/RU/out/CYR_marketingFalse_RU.csv      | /test/Intourist-RU | yyyy-MM-dd  |
      | /file/RU/in/CYR_marketingEmpty_RU.csv      | /file/RU/out/CYR_marketingEmpty_RU.csv      | /test/Intourist-RU | yyyy-MM-dd  |

      | /file/UK/in/CYR_input16_UK.csv             | /file/UK/out/CYR_input16_UK.csv             | /test/GBG-GB       | yyyy-MM-dd  |



