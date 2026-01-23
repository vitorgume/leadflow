import base64
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from cryptography.exceptions import InvalidTag


# --- Constantes (Ajuste conforme sua implementação Java) ---

# Tamanho do Nonce (IV) em bytes. 12 é o recomendado para GCM.
NONCE_SIZE_BYTES = 12
# Tamanho da Tag de Autenticação em bytes. 16 (128 bits) é o padrão.
TAG_SIZE_BYTES = 16


class CryptoUtil:


    def __init__(self, secret_key: str):
        if not secret_key:
            raise ValueError("A chave secreta não pode ser vazia.")
        # Assumindo que a chave secreta já está nos bytes corretos ou string que pode ser codificada.
        self.key = secret_key.encode('utf-8') if isinstance(secret_key, str) else secret_key


    def decrypt(self, encrypted_data_b64: str) -> str:
        try:
            encrypted_data = base64.b64decode(encrypted_data_b64)

            if len(encrypted_data) < NONCE_SIZE_BYTES + TAG_SIZE_BYTES:
                raise ValueError("Dados criptografados são muito curtos para serem válidos.")

            # Extrair as partes da estrutura: [Nonce] + [Ciphertext + Tag]
            nonce = encrypted_data[:NONCE_SIZE_BYTES]
            ciphertext_and_tag = encrypted_data[NONCE_SIZE_BYTES:]

            aesgcm = AESGCM(self.key)
            decrypted_data = aesgcm.decrypt(nonce, ciphertext_and_tag, None)

            return decrypted_data.decode('utf-8')

        except (base64.binascii.Error, ValueError) as e:
            # Captura erros de decodificação ou de tamanho
            raise ValueError(f"Erro ao processar dados criptografados: {e}")
        except InvalidTag:
            # A chave pode estar errada ou os dados foram corrompidos
            raise InvalidTag("Falha na descriptografia: a tag de autenticação é inválida. Verifique a chave e a integridade dos dados.")
        except Exception as e:
            raise Exception(f"Ocorreu um erro inesperado durante a descriptografia: {e}")
