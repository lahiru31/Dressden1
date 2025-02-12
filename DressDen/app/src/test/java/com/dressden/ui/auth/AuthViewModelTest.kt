package com.dressden.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dressden.data.model.User
import com.dressden.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val userRepository: UserRepository = mock()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `when sign in succeeds, authState should be Success`() = testDispatcher.runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val user = User(
            id = "123",
            email = email,
            displayName = "Test User",
            phoneNumber = null,
            profileImageUrl = null
        )
        whenever(userRepository.signIn(email, password)).thenReturn(Result.success(user))

        // When
        viewModel.signIn(email, password)

        // Then
        assert(viewModel.authState.value is AuthState.Success)
    }

    @Test
    fun `when sign in fails, authState should be Error`() = testDispatcher.runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Invalid credentials"
        whenever(userRepository.signIn(email, password)).thenReturn(Result.failure(Exception(errorMessage)))

        // When
        viewModel.signIn(email, password)

        // Then
        assert(viewModel.authState.value is AuthState.Error)
        assert((viewModel.authState.value as AuthState.Error).message == errorMessage)
    }

    @Test
    fun `when sign up succeeds, authState should be Success`() = testDispatcher.runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val displayName = "Test User"
        val user = User(
            id = "123",
            email = email,
            displayName = displayName,
            phoneNumber = null,
            profileImageUrl = null
        )
        whenever(userRepository.signUp(email, password, displayName)).thenReturn(Result.success(user))

        // When
        viewModel.signUp(email, password, displayName)

        // Then
        assert(viewModel.authState.value is AuthState.Success)
    }

    @Test
    fun `when sign up fails, authState should be Error`() = testDispatcher.runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val displayName = "Test User"
        val errorMessage = "Email already exists"
        whenever(userRepository.signUp(email, password, displayName))
            .thenReturn(Result.failure(Exception(errorMessage)))

        // When
        viewModel.signUp(email, password, displayName)

        // Then
        assert(viewModel.authState.value is AuthState.Error)
        assert((viewModel.authState.value as AuthState.Error).message == errorMessage)
    }

    @Test
    fun `when reset password succeeds, authState should be ResetPasswordSuccess`() = testDispatcher.runBlockingTest {
        // Given
        val email = "test@example.com"
        whenever(userRepository.resetPassword(email)).thenReturn(Unit)

        // When
        viewModel.resetPassword(email)

        // Then
        assert(viewModel.authState.value is AuthState.ResetPasswordSuccess)
    }

    @Test
    fun `when reset password fails, authState should be Error`() = testDispatcher.runBlockingTest {
        // Given
        val email = "test@example.com"
        val errorMessage = "User not found"
        whenever(userRepository.resetPassword(email)).thenThrow(Exception(errorMessage))

        // When
        viewModel.resetPassword(email)

        // Then
        assert(viewModel.authState.value is AuthState.Error)
        assert((viewModel.authState.value as AuthState.Error).message == errorMessage)
    }

    @Test
    fun `when email is invalid, validateEmail should return false`() {
        // Given
        val invalidEmails = listOf(
            "",
            "invalid",
            "invalid@",
            "@domain.com",
            "invalid@domain",
            "invalid@domain."
        )

        // When/Then
        invalidEmails.forEach { email ->
            assert(!viewModel.validateEmail(email)) {
                "Email '$email' should be invalid"
            }
        }
    }

    @Test
    fun `when email is valid, validateEmail should return true`() {
        // Given
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.com",
            "user+tag@domain.co.uk",
            "123@domain.com"
        )

        // When/Then
        validEmails.forEach { email ->
            assert(viewModel.validateEmail(email)) {
                "Email '$email' should be valid"
            }
        }
    }

    @Test
    fun `when password is too short, validatePassword should return false`() {
        // Given
        val shortPasswords = listOf(
            "",
            "a",
            "12345"
        )

        // When/Then
        shortPasswords.forEach { password ->
            assert(!viewModel.validatePassword(password)) {
                "Password '$password' should be invalid"
            }
        }
    }

    @Test
    fun `when password is valid, validatePassword should return true`() {
        // Given
        val validPasswords = listOf(
            "password123",
            "123456",
            "abcdef",
            "Pass@word1"
        )

        // When/Then
        validPasswords.forEach { password ->
            assert(viewModel.validatePassword(password)) {
                "Password '$password' should be valid"
            }
        }
    }

    @Test
    fun `when display name is too short, validateDisplayName should return false`() {
        // Given
        val shortNames = listOf(
            "",
            "a",
            "ab"
        )

        // When/Then
        shortNames.forEach { name ->
            assert(!viewModel.validateDisplayName(name)) {
                "Display name '$name' should be invalid"
            }
        }
    }

    @Test
    fun `when display name is valid, validateDisplayName should return true`() {
        // Given
        val validNames = listOf(
            "John",
            "Jane Doe",
            "User123"
        )

        // When/Then
        validNames.forEach { name ->
            assert(viewModel.validateDisplayName(name)) {
                "Display name '$name' should be valid"
            }
        }
    }
}
