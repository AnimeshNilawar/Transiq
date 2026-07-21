import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import useAuth from '@/hooks/useAuth'
import { Loader2 } from 'lucide-react'

const registerSchema = z.object({
  businessName: z.string().min(1, 'Business name is required'),
  businessEmail: z.string().email('Please enter a valid business email'),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  email: z.string().email('Please enter a valid email').optional().or(z.literal('')),
  password: z.string().min(6, 'Password must be at least 6 characters').optional().or(z.literal('')),
})

export default function RegisterPage() {
  const { register: registerUser } = useAuth()
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(registerSchema),
  })

  const onSubmit = async (data) => {
    setIsLoading(true)
    try {
      const payload = {
        businessName: data.businessName,
        businessEmail: data.businessEmail,
      }
      if (data.firstName) payload.firstName = data.firstName
      if (data.lastName) payload.lastName = data.lastName
      if (data.email) payload.email = data.email
      if (data.password) payload.password = data.password

      await registerUser(payload)
      navigate('/login')
    } catch {
      // Error is handled by the axios interceptor
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <div className="w-full max-w-sm space-y-6">
        <div className="text-center">
          <h1 className="text-2xl font-bold tracking-tight text-foreground">Transiq</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Create your merchant account
          </p>
        </div>

        <div className="bg-card rounded-lg border border-border p-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label
                htmlFor="businessName"
                className="block text-sm font-medium mb-1.5 text-card-foreground"
              >
                Business Name *
              </label>
              <input
                id="businessName"
                {...register('businessName')}
                className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                placeholder="Acme Corp"
              />
              {errors.businessName && (
                <p className="text-sm text-destructive mt-1">
                  {errors.businessName.message}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="businessEmail"
                className="block text-sm font-medium mb-1.5 text-card-foreground"
              >
                Business Email *
              </label>
              <input
                id="businessEmail"
                type="email"
                {...register('businessEmail')}
                className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                placeholder="billing@acme.com"
              />
              {errors.businessEmail && (
                <p className="text-sm text-destructive mt-1">
                  {errors.businessEmail.message}
                </p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label
                  htmlFor="firstName"
                  className="block text-sm font-medium mb-1.5 text-card-foreground"
                >
                  First Name
                </label>
                <input
                  id="firstName"
                  {...register('firstName')}
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  placeholder="John"
                />
              </div>
              <div>
                <label
                  htmlFor="lastName"
                  className="block text-sm font-medium mb-1.5 text-card-foreground"
                >
                  Last Name
                </label>
                <input
                  id="lastName"
                  {...register('lastName')}
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  placeholder="Doe"
                />
              </div>
            </div>

            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium mb-1.5 text-card-foreground"
              >
                Login Email
              </label>
              <input
                id="email"
                type="email"
                {...register('email')}
                className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                placeholder="john@acme.com"
              />
              {errors.email && (
                <p className="text-sm text-destructive mt-1">
                  {errors.email.message}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium mb-1.5 text-card-foreground"
              >
                Password
              </label>
              <input
                id="password"
                type="password"
                {...register('password')}
                className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                placeholder="••••••••"
              />
              {errors.password && (
                <p className="text-sm text-destructive mt-1">
                  {errors.password.message}
                </p>
              )}
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full inline-flex items-center justify-center rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 disabled:opacity-50 transition-colors"
            >
              {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Create Account
            </button>
          </form>
        </div>

        <p className="text-center text-sm text-muted-foreground">
          Already have an account?{' '}
          <Link to="/login" className="text-accent font-medium hover:underline">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  )
}
