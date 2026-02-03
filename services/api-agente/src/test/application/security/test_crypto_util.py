import pytest
import base64
import hashlib
from unittest.mock import patch, Mock
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from cryptography.exceptions import InvalidTag

from src.application.security.crypto_util import CryptoUtil, NONCE_SIZE_BYTES, TAG_SIZE_BYTES

# --- Fixtures ---

@pytest.fixture
def secret_key():
    return "my_super_secret_key_for_testing"

@pytest.fixture
def crypto_util(secret_key):
    return CryptoUtil(secret_key)

@pytest.fixture
def aesgcm_mock():
    with patch('src.application.security.crypto_util.AESGCM') as mock:
        yield mock

# --- Tests for Constructor ---

def test_crypto_util_init_success(secret_key):
    crypto = CryptoUtil(secret_key)
    sha256 = hashlib.sha256()
    sha256.update(secret_key.encode('utf-8'))
    assert crypto.key == sha256.digest()

def test_crypto_util_init_empty_secret_key_raises_value_error():
    with pytest.raises(ValueError, match="A chave secreta não pode ser vazia."):
        CryptoUtil("")

# --- Tests for Decrypt Method ---

def test_decrypt_success(crypto_util, aesgcm_mock):
    mock_aesgcm_instance = aesgcm_mock.return_value
    mock_aesgcm_instance.decrypt.return_value = b"decrypted_data"

    # Simulate encrypted data structure: Nonce + Ciphertext + Tag
    test_nonce = b'\x00' * NONCE_SIZE_BYTES
    test_ciphertext_and_tag = b'some_ciphertext' + b'\x00' * TAG_SIZE_BYTES
    encrypted_data_bytes = test_nonce + test_ciphertext_and_tag
    encrypted_data_b64 = base64.b64encode(encrypted_data_bytes).decode('utf-8')

    result = crypto_util.decrypt(encrypted_data_b64)

    mock_aesgcm_instance.decrypt.assert_called_once_with(
        test_nonce,
        test_ciphertext_and_tag,
        None
    )
    assert result == "decrypted_data"

def test_decrypt_encrypted_data_too_short_raises_value_error(crypto_util):
    # Data shorter than Nonce + Tag
    encrypted_data_b64 = base64.b64encode(b'short_data').decode('utf-8')
    with pytest.raises(ValueError, match="Dados criptografados são muito curtos para serem válidos."):
        crypto_util.decrypt(encrypted_data_b64)

def test_decrypt_invalid_base64_raises_value_error(crypto_util):
    # Invalid base64 string
    encrypted_data_b64 = "this-is-not-valid-base64!"
    with pytest.raises(ValueError, match="Erro ao processar dados criptografados: Dados criptografados são muito curtos para serem válidos."):
        crypto_util.decrypt(encrypted_data_b64)

def test_decrypt_invalid_tag_raises_invalid_tag_exception(crypto_util, aesgcm_mock):
    mock_aesgcm_instance = aesgcm_mock.return_value
    mock_aesgcm_instance.decrypt.side_effect = InvalidTag("mocked invalid tag")

    test_nonce = b'\x00' * NONCE_SIZE_BYTES
    test_ciphertext_and_tag = b'some_ciphertext' + b'\x00' * TAG_SIZE_BYTES
    encrypted_data_bytes = test_nonce + test_ciphertext_and_tag
    encrypted_data_b64 = base64.b64encode(encrypted_data_bytes).decode('utf-8')

    with pytest.raises(InvalidTag, match="Falha na descriptografia: a tag de autenticação é inválida."):
        crypto_util.decrypt(encrypted_data_b64)

def test_decrypt_unexpected_exception_raises_general_exception(crypto_util, aesgcm_mock):
    mock_aesgcm_instance = aesgcm_mock.return_value
    mock_aesgcm_instance.decrypt.side_effect = Exception("An unexpected error occurred")

    test_nonce = b'\x00' * NONCE_SIZE_BYTES
    test_ciphertext_and_tag = b'some_ciphertext' + b'\x00' * TAG_SIZE_BYTES
    encrypted_data_bytes = test_nonce + test_ciphertext_and_tag
    encrypted_data_b64 = base64.b64encode(encrypted_data_bytes).decode('utf-8')

    with pytest.raises(Exception, match="Ocorreu um erro inesperado durante a descriptografia: An unexpected error occurred"):
        crypto_util.decrypt(encrypted_data_b64)
