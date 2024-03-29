-- \connect "pipegine"

-- Cria um usuário com username 'vini' e senha 'password'

insert into pipegine_platform.application_user(id, name, username, password, is_account_non_expired,
    is_account_nonLocked, is_credentials_non_expired, is_enabled)
    values ('78cec5db-6396-4fd9-803f-1fd469d76330'::uuid, 'Vinicius', 'vini',
        '$2a$10$M2lu6nBJ4yCdJ/qtbT7aZeofmxsFqYlMQtz1M2QQgRm1sCwTi2i/m', true, true, true, true);

-- Cria um usuário com username 'arthur' e senha '123'

insert into pipegine_platform.application_user(id, name, username, password, is_account_non_expired,
                                               is_account_nonLocked, is_credentials_non_expired, is_enabled)
values ('9780ed75-52db-4a4d-a0ad-13681a28e00a'::uuid, 'teste2', 'arthur',
        '$2a$10$XbQdgwykWtY7no9nNJTVO.97cSRNZx2b6MZ9AYnTNNtoDWX0Q9zxi', true, true, true, true);

-- Cria grupos para vini

insert into pipegine_platform.group(id, owner_id)
values ('cdcb7005-6387-4184-b9a1-ebe620daf3b4'::uuid, '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid);

insert into pipegine_platform.group(id, owner_id)
values ('52dea72e-bf07-40db-8484-ba5879cbac18'::uuid, '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid);

-- Cria participações nos grupos

insert into pipegine_platform.group_participation(id, group_id, receive_user_id,
                                                  submitter_user_id, create_date, status)
values ('ad76cd44-dc3c-4837-9c35-6bd0bd48bbc8'::uuid, 'cdcb7005-6387-4184-b9a1-ebe620daf3b4'::uuid,
        '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid, '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
        CURRENT_DATE, 'ACCEPTED');

insert into pipegine_platform.group_participation(id, group_id, receive_user_id,
                                                  submitter_user_id, create_date, status)
values ('538b32de-49f3-43df-9da0-73e3730ae06f'::uuid, '52dea72e-bf07-40db-8484-ba5879cbac18'::uuid,
        '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid, '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
        CURRENT_DATE, 'ACCEPTED');


-- cria provider para grafico pre processamento
insert into pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types,
                                       owner_id, operations)
    values('78cec5db-6396-4fd9-803f-1fd469d76312'::uuid, 'Grafico pré processamento',
           'Exporta grafico do resultante do preprocessamento', 'http://localhost:5011',
           'https://github.com/lucas-ifsp/pipegene-services.git', true,
           'maf','png', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
           to_json('[{
            "type": "column",
            "description": "Grafico resultante do pré processamento",
            "params": [
                {
                    "type": "text",
                    "name": "colunas desejadas",
                    "key": "columns",
                    "example": "Hugo_Symbol, Chromosome"
                }
            ]
        }]'::jsonb));

-- adiciona provider aos grupos

insert into pipegine_platform.group_provider(group_id, provider_id)
    values('cdcb7005-6387-4184-b9a1-ebe620daf3b4'::uuid, '78cec5db-6396-4fd9-803f-1fd469d76312'::uuid);

-- cria projeto que vai utilizar do provider de Grafico pré processamento
insert into pipegine_platform.project(id, name, description, group_id, owner_id)
    values ('f2d6a949-8bb5-4df5-8ca7-e5b8d2292488'::uuid, 'Explorando pré processamentos MAF',
            'Analise de gráficos resultante de pre processamentos de arquivos MAF',
            'cdcb7005-6387-4184-b9a1-ebe620daf3b4'::uuid,
            '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid);

-- cria dataset do projeto que servira de input para o Grafico pré processamento
insert into pipegine_platform.dataset(id, filename, project_id)
    values('042e4e39-ba0b-49d1-a01a-237333d0b1a5'::uuid,
           '042e4e39-ba0b-49d1-a01a-237333d0b1a5_uploads_mock_GBM_MEMo.maf', 'f2d6a949-8bb5-4df5-8ca7-e5b8d2292488'::uuid);

-- cria pipeline para o Grafico pré processamento
INSERT INTO pipegine_platform.pipeline(id, project_id, description, status)
    VALUES('959f2ff3-ed29-435b-83bb-c8390f6385bf'::uuid, 'f2d6a949-8bb5-4df5-8ca7-e5b8d2292488'::uuid, 'Exporta grafico resultante do pre processamento', 'ENABLED');

-- cria step unico para o Grafico pré processamento
INSERT INTO pipegine_platform.pipeline_step(step_id, pipeline_id, provider_id, input_type, output_type, params, step_number)
    VALUES('715d7d76-b5d1-4d45-b1f6-66b46d3c2964'::uuid, '959f2ff3-ed29-435b-83bb-c8390f6385bf'::uuid,
        '78cec5db-6396-4fd9-803f-1fd469d76312'::uuid, 'maf', 'png',
        (to_json('{"columns": "Hugo_Symbol, Chromosome, Start_Position, End_Position, Reference_Allele, Tumor_Seq_Allele2, Variant_Classification, Variant_Type, Tumor_Sample_Barcode"}'::jsonb)),
           1);



-- cria novo provider que realiza o pre processamento exportando um maf que sera utilizado de input
insert into pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types,
                                       owner_id, operations)
values('e8bf42e4-2ffc-4935-a546-ee5d9263f419'::uuid, 'Pré processamento - Rodrigo',
       'Realiza o pré processamento de arquivos maf e txt para maf', 'http://localhost:5001',
       'https://github.com/lucas-ifsp/pipegene-services.git', false,
       'txt,maf','maf', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
       to_json('[{
            "type": "column",
            "description": "Pré processamento de arquivo maf e txt",
            "params": [
                {
                    "type": "text",
                    "name": "colunas desejadas",
                    "key": "columns",
                    "example": "Hugo_Symbol, Chromosome"
                }
            ]
        }]'::jsonb));

-- cria novo provider que recebera como input o maf resultando do pre processamento e fara a classificação da variante
insert into pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types,
                                       owner_id, operations)
values('49df4595-b8af-4e32-8791-65e583ae08a2'::uuid, 'Classificação de variante - Rodrigo',
       'Partindo de um maf pré processado realiza a classificação de variante', 'http://localhost:5002',
       'https://github.com/lucas-ifsp/pipegene-services.git', true,
       'maf','png', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
       to_json('[{
            "type": "column",
            "description": "Classificação de variante",
            "params": [
                {
                    "type": "text",
                    "name": "colunas desejadas",
                    "key": "columns",
                    "example": "Hugo_Symbol, Chromosome"
                }
            ]
        }]'::jsonb));


-- cria projeto que vai utilizar do provider de Pré processamento - Rodrigo e Classificação de variante - Rodrigo
INSERT INTO pipegine_platform.project (id, name, description, group_id, owner_id)
VALUES ('28559f11-0c41-4444-a087-c14efde8b482', 'Pré processamento - Rodrigo e Classificação de variante - Rodrigo',
        'Recebe um input e devolve um maf para ser utilizado como input em outro serviço',
        '52dea72e-bf07-40db-8484-ba5879cbac18'::uuid,
        '78cec5db-6396-4fd9-803f-1fd469d76330');

-- cria group_provider para o provider de Pré processamento - Rodrigo
INSERT INTO pipegine_platform.group_provider (group_id, provider_id)
VALUES ('cdcb7005-6387-4184-b9a1-ebe620daf3b4'::uuid, 'e8bf42e4-2ffc-4935-a546-ee5d9263f419'::uuid);

-- cria dataset do projeto que servira de input para o Pré processamento - Rodrigo
INSERT INTO pipegine_platform.dataset (id, filename, project_id)
VALUES ('e1a8ba35-d029-4a7c-bb98-ae7e84c41a41',
        'e1a8ba35-d029-4a7c-bb98-ae7e84c41a41_uploads_BRCA2_data_mutations_extended.maf',
        '28559f11-0c41-4444-a087-c14efde8b482');

-- cria pipeline para o Pré processamento - Rodrigo e Classificação de variante - Rodrigo
INSERT INTO pipegine_platform.pipeline (id, project_id, description, status)
VALUES ('3677aa2e-6be8-43f9-ad8c-a0f1f71e2040', '28559f11-0c41-4444-a087-c14efde8b482', 'Realiza pre processamento e faz a classificação de variante - Rodrigo', 'ENABLED');

-- cria steps para o Pré processamento - Rodrigo e Classificação de variante - Rodrigo
INSERT INTO pipegine_platform.pipeline_step (step_id, pipeline_id, provider_id, input_type, output_type, params, step_number)
VALUES ('7b97b9b1-346b-47ed-aeef-bee8bbdcf064', '3677aa2e-6be8-43f9-ad8c-a0f1f71e2040', 'e8bf42e4-2ffc-4935-a546-ee5d9263f419',
        'maf', 'maf', '{"columns": "Hugo_Symbol,Chromosome,Start_Position,End_Position,Reference_Allele,Tumor_Seq_Allele2,Variant_Classification,Variant_Type,Tumor_Sample_Barcode"}', 1);

INSERT INTO pipegine_platform.pipeline_step (step_id, pipeline_id, provider_id, input_type, output_type, params, step_number)
 VALUES ('2fe129d3-f3d2-46c5-b34a-e472ed217b07', '3677aa2e-6be8-43f9-ad8c-a0f1f71e2040', '49df4595-b8af-4e32-8791-65e583ae08a2',
         'maf', 'png', '{"columns": "Hugo_Symbol,Chromosome,Start_Position,End_Position,Reference_Allele,Tumor_Seq_Allele2,Variant_Classification,Variant_Type,Tumor_Sample_Barcode"}', 2);

-- QUERIES SERVIÇOS PRISCILA

INSERT INTO pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types, owner_id, operations)
VALUES ('2147af4c-a4ca-4e11-98b5-e6fc8bfe7d0e'::uuid, 'Compute Gene Incidence', 'Este serviço computa a incidência genética com base em um arquivo MAF pré-processado. Ele utiliza os dados de variantes para calcular a incidência genética, produzindo uma visualização em formato PNG.', 'http://localhost:5012/', 'https://github.com/lucas-ifsp/pipegene-services.git', true, 'maf', 'png', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
        to_json('[{
          "type": "column",
          "description": "Compute Gene Incidence",
          "params": [
            {
              "type": "text",
              "name": "colunas desejadas",
              "key": "columns",
              "example": "Hugo_Symbol, Chromosome"
            }
          ]
        }]'::jsonb));

INSERT INTO pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types, owner_id, operations)
VALUES ('25d6d899-9c8b-4833-8f13-04684648bf1d'::uuid, 'Plot Gene Bar Chart', 'Este serviço gera um gráfico de barras de genes com base em um conjunto de dados de variantes. Utilizando um formato de entrada JSON, produz um gráfico PNG que visualiza a distribuição de genes.', 'http://localhost:5017/', 'https://github.com/lucas-ifsp/pipegene-services.git', true, 'json', 'png', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
        to_json('[{
          "type": "column",
          "description": "Plot Gene Bar Chart",
          "params": [
            {
              "type": "text",
              "name": "colunas desejadas",
              "key": "columns",
              "example": "Hugo_Symbol, Chromosome"
            }
          ]
        }]'::jsonb));

INSERT INTO pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types, owner_id, operations)
VALUES ('5b20c543-0254-44ac-b572-c3f267abc3f2'::uuid, 'Compute Mutation Incidence', 'Este serviço calcula a incidência de mutações com base em um arquivo MAF pré-processado. Utiliza os dados de variantes para calcular a incidência de mutações, gerando um formato de saída JSON que descreve a incidência.', 'http://localhost:5016/', 'https://github.com/lucas-ifsp/pipegene-services.git', true, 'tsv', 'json', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
        to_json('[{
          "type": "column",
          "description": "Compute Mutation Incidence",
          "params": [
            {
              "type": "text",
              "name": "colunas desejadas",
              "key": "columns",
              "example": "Hugo_Symbol, Chromosome"
            }
          ]
        }]'::jsonb));

INSERT INTO pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types, owner_id, operations)
VALUES ('6147edd6-4b8d-4481-8bd4-6a40ecbaa19f'::uuid, 'Plot Mutation Barchart Snv', 'Este serviço gera um gráfico de barras de mutações SNV (Single Nucleotide Variant) com base em um conjunto de dados de variantes. Aceita um formato de entrada TSV e produz um gráfico em formato JSON que visualiza as mutações.', 'http://localhost:5014/', 'https://github.com/lucas-ifsp/pipegene-services.git', true, 'tsv', 'json', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
        to_json('[{
          "type": "column",
          "description": "Plot Mutation Barchart Snv",
          "params": [
            {
              "type": "text",
              "name": "colunas desejadas",
              "key": "columns",
              "example": "Hugo_Symbol, Chromosome"
            }
          ]
        }]'::jsonb));

INSERT INTO pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types, owner_id, operations)
VALUES ('944df70f-120a-472d-8a36-60026873d2b3'::uuid, 'Compute Top Ten Mutation', 'Este serviço computa as dez principais mutações com base em um arquivo TSV de variantes. Utiliza os dados para identificar as dez mutações mais comuns, produzindo um formato de saída TSV descrevendo essas mutações.', 'http://localhost:5011/', 'https://github.com/lucas-ifsp/pipegene-services.git', true, 'tsv', 'tsv', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
        to_json('[{
          "type": "column",
          "description": "Compute Top Ten Mutation",
          "params": [
            {
              "type": "text",
              "name": "colunas desejadas",
              "key": "columns",
              "example": "Hugo_Symbol, Chromosome"
            }
          ]
        }]'::jsonb));

INSERT INTO pipegine_platform.provider(id, name, description, url, url_source, public, input_supported_types, output_supported_types, owner_id, operations)
VALUES ('12dd474e-1d1c-4bda-999e-bf2a7dfbf465'::uuid, 'Plot Composite Bar Chart', 'Este serviço gera um gráfico de barras compostas com base em um conjunto de dados de variantes. Utiliza um formato de entrada JSON e produz um gráfico em formato PNG que compara diferentes tipos de mutações.', 'http://localhost:5015/', 'https://github.com/lucas-ifsp/pipegene-services.git', true, 'json', 'png', '78cec5db-6396-4fd9-803f-1fd469d76330'::uuid,
        to_json('[{
          "type": "column",
          "description": "Plot Composite Bar Chart",
          "params": [
            {
              "type": "text",
              "name": "colunas desejadas",
              "key": "columns",
              "example": "Hugo_Symbol, Chromosome"
            }
          ]
        }]'::jsonb));


