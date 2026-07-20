import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getUsers, inviteUser, updateUserRole, deleteUser } from '@/api/users'
import { toast } from 'sonner'

/**
 * Query hook to fetch all users
 */
export function useUsers() {
  return useQuery({
    queryKey: ['users'],
    queryFn: () => getUsers().then((res) => res.data),
  })
}

/**
 * Mutation hook to invite a user
 */
export function useInviteUser() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: inviteUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })
      toast.success('User invited')
    },
  })
}

/**
 * Mutation hook to update a user's role
 */
export function useUpdateUserRole() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }) => updateUserRole(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })
      toast.success('User role updated')
    },
  })
}

/**
 * Mutation hook to delete a user
 */
export function useDeleteUser() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: deleteUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })
      toast.success('User removed')
    },
  })
}
